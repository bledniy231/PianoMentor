package teachingsolutions.domain_layer.user

import android.content.SharedPreferences
import teachingsolutions.data_access_layer.DAL_models.user.JwtTokens
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequest
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponse
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequestApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequestApi
import teachingsolutions.data_access_layer.user.UserDataSource
import teachingsolutions.data_access_layer.shared_preferences_keys.SharedPreferencesKeys
import teachingsolutions.domain_layer.common.CustomGsonSupplier
import teachingsolutions.domain_layer.courses.CoursesRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val prefKeys: SharedPreferencesKeys,
    private val dataSource: UserDataSource,
    private val customGsonSupplier: CustomGsonSupplier,
    private val coursesRepository: CoursesRepository
) {

    private var user: LoginUserResponse? = null
    private val gson = customGsonSupplier.getCustomGsonObject()

    val userId: Long?
        get() = user?.userId

    val userName: String?
        get() = user?.userName

    val isLoggedIn: Boolean
        get() = user != null

    private var isFirstCheckUserAvailability: Boolean = true

    init {
        user = null
    }

    public fun getUserAuthToken(): String {
        return user?.jwtTokensModel?.accessToken ?: ""
    }

    suspend fun logout(isAccessTokenExpired: Boolean) {
        if (!isAccessTokenExpired) {
            dataSource.logout()
        }

        coursesRepository.clearCache()
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
            setLoggedInUser(result.data, true)
        }

        return result
    }

    suspend fun register(username: String, email: String, password: String, confirmPassword: String): ActionResult<LoginUserResponse> {
        val result = dataSource.register(RegisterUserRequestApi(username, email, password, confirmPassword, listOf("user")))

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data, true)
        }

        return result
    }

    private suspend fun refreshUserTokens(jwtTokens: JwtTokens): ActionResult<RefreshUserTokensResponseApi> {
        return dataSource.refreshUserTokens(RefreshUserTokensRequestApi(jwtTokens))
    }

    private fun setLoggedInUser(loginUserResponse: LoginUserResponse, addToSharedPrefs: Boolean) {
        this.user = loginUserResponse

        if (addToSharedPrefs) {
            with(sharedPreferences.edit()) {
                putString(prefKeys.KEY_USER_TOKENS, gson.toJson(loginUserResponse.jwtTokensModel))
                putString(prefKeys.KEY_USERID, loginUserResponse.userId.toString())
                putString(prefKeys.KEY_USERNAME, loginUserResponse.userName)
                putString(prefKeys.KEY_EMAIL, loginUserResponse.email)
                putStringSet(prefKeys.KEY_ROLES, loginUserResponse.roles.toSet())
                apply()
            }
        }
    }

    public suspend fun refreshUserIfNeeds(): Boolean {
        //sharedPreferences.edit().clear().apply()
        if (!isFirstCheckUserAvailability) {
            return true
        }

        isFirstCheckUserAvailability = false
        val jwtTokens = sharedPreferences.getString(prefKeys.KEY_USER_TOKENS, null)?.let { gson.fromJson(it, JwtTokens::class.java) }
        val userId = sharedPreferences.getString(prefKeys.KEY_USERID, null)?.toLong()
        val userName = sharedPreferences.getString(prefKeys.KEY_USERNAME, null)
        val email = sharedPreferences.getString(prefKeys.KEY_EMAIL, null)
        val roles = sharedPreferences.getStringSet(prefKeys.KEY_ROLES, null)?.toList()

        if (jwtTokens != null && userId != null && userName != null && email != null && roles != null)
        {
            if (!isTokenExpired(jwtTokens.accessTokenExpireTime) && !isTokenExpired(jwtTokens.refreshTokenExpireTime))
            {
                setLoggedInUser(LoginUserResponse(userId, userName, email, jwtTokens, roles), false)
                return true
            }
            else if (!isTokenExpired(jwtTokens.refreshTokenExpireTime))
            {
                val result = refreshUserTokens(jwtTokens)
                if (result is ActionResult.Success)
                {
                    result.data.jwtTokens?.let { newTokens ->
                        setLoggedInUser(LoginUserResponse(userId, userName, email, newTokens, roles), addToSharedPrefs = true)
                    }
                    return true
                }
                logout(true)
                return false
            }
            else
            {
                logout(true)
                return false
            }
        }
        else
        {
            return false
        }
    }

    private fun isTokenExpired(tokenExpireTime: LocalDateTime?): Boolean {
        return tokenExpireTime?.isBefore(LocalDateTime.now(ZoneOffset.UTC)) ?: false
    }
}