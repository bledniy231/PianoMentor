package teachingsolutions.data_access_layer.login_registration

import android.content.SharedPreferences
import com.google.gson.Gson
import teachingsolutions.data_access_layer.DAL_models.user.JwtTokens
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequest
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponse
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequest
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponse
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequest
import teachingsolutions.data_access_layer.shared_preferences_keys.SharedPreferencesKeys
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val prefKeys: SharedPreferencesKeys,
    private val dataSource: UserDataSource) {

    private var user: LoginUserResponse? = null
    private val gson = Gson()

    val userId: Long?
        get() = user?.userId

    val userName: String?
        get() = user?.userName

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    suspend fun logout() {
        dataSource.logout()
        user = null
        with(sharedPreferences.edit()) {
            prefKeys::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                val key = field.get(prefKeys) as? String
                if (key != null) {
                    if (key == prefKeys.KEY_ROLES) {
                        putStringSet(key, null)
                    } else {
                        putString(key, null)
                    }
                }
            }
            apply()
        }
    }

    suspend fun login(username: String, password: String): ActionResult<LoginUserResponse> {
        val result = dataSource.login(LoginUserRequest(username, password))

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    suspend fun register(username: String, email: String, password: String, confirmPassword: String): ActionResult<LoginUserResponse> {
        val result = dataSource.register(RegisterUserRequest(username, email, password, confirmPassword, listOf("user")))

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    suspend fun refreshUserTokens(jwtTokens: JwtTokens): ActionResult<RefreshUserTokensResponse> {
        return dataSource.refreshUserTokens(RefreshUserTokensRequest(jwtTokens))
    }

    private fun setLoggedInUser(loginUserResponse: LoginUserResponse) {
        this.user = loginUserResponse

        with(sharedPreferences.edit()) {
            putString(prefKeys.KEY_USER_TOKENS, gson.toJson(loginUserResponse.jwtTokensModel))
            putString(prefKeys.KEY_USERID, loginUserResponse.userId.toString())
            putString(prefKeys.KEY_USERNAME, loginUserResponse.userName)
            putString(prefKeys.KEY_EMAIL, loginUserResponse.email)
            putStringSet(prefKeys.KEY_ROLES, loginUserResponse.roles.toSet())
            apply()
        }
    }

    public suspend fun checkIfCurrentUserValid(): LoginUserResponse? {
        val jwtTokensJsonString = sharedPreferences.getString(prefKeys.KEY_USER_TOKENS, null)
        val jwtTokens = if (jwtTokensJsonString != null) gson.fromJson(jwtTokensJsonString, JwtTokens::class.java) else null
        val userId = sharedPreferences.getString(prefKeys.KEY_USERID, null)?.toLong()
        val userName = sharedPreferences.getString(prefKeys.KEY_USERNAME, null)
        val email = sharedPreferences.getString(prefKeys.KEY_EMAIL, null)
        val roles = sharedPreferences.getStringSet(prefKeys.KEY_ROLES, null)?.toList()

        val user: LoginUserResponse? = null
        if (jwtTokens != null && userId != null && userName != null && email != null && !roles.isNullOrEmpty()) {
            if (isTokensValid(jwtTokens))
            {
                return LoginUserResponse(userId, userName, email, jwtTokens, roles)
            }
            else if (isTokenExpired(jwtTokens.accessTokenExpireTime))
            {
                val refreshResult = refreshUserTokens(jwtTokens)
                if (refreshResult is ActionResult.Success)
                {
                    return refreshResult.data.jwtTokens?.let { LoginUserResponse(userId, userName, email, it, roles) }
                }
                else
                {
                    logout()
                    return null
                }
            }
        }
        else
        {
            return null
        }
    }

    private fun isTokensValid(jwtTokens: JwtTokens): Boolean {
        return jwtTokens.accessTokenExpireTime?.isAfter(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)) == true
            && jwtTokens.refreshTokenExpireTime?.isAfter(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)) == true
    }

    private fun isTokenExpired(tokenExpireTime: LocalDateTime?): Boolean {
        return tokenExpireTime?.isBefore(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)) == true
    }
}