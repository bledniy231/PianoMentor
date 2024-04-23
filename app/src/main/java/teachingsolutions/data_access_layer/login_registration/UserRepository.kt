package teachingsolutions.data_access_layer.login_registration

import android.content.SharedPreferences
import teachingsolutions.data_access_layer.DAL_models.LoginUserRequest
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.LoginUserResponse
import teachingsolutions.data_access_layer.DAL_models.RegisterUserRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val dataSource: UserDataSource
) {
    companion object {
        private const val PREF_NAME = "teaching_solutions"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USERID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLES = "roles"
    }

    private var user: LoginUserResponse? = null
        private set

    val userId: Long?
        get() = user?.userId

    val userName: String?
        get() = user?.userName

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = getUserFromPreferences()
    }

    suspend fun logout() {
        dataSource.logout()
        user = null
        with(sharedPreferences.edit()) {
            putString(KEY_AUTH_TOKEN, null)
            putString(KEY_REFRESH_TOKEN, null)
            putString(KEY_USERID, null)
            putString(KEY_USERNAME, null)
            putString(KEY_EMAIL, null)
            putStringSet(KEY_ROLES, null)
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

    private fun setLoggedInUser(loginUserResponse: LoginUserResponse) {
        this.user = loginUserResponse

        with(sharedPreferences.edit()) {
            putString(KEY_AUTH_TOKEN, loginUserResponse.accessToken)
            putString(KEY_REFRESH_TOKEN, loginUserResponse.refreshToken)
            putString(KEY_USERID, loginUserResponse.userId.toString())
            putString(KEY_USERNAME, loginUserResponse.userName)
            putString(KEY_EMAIL, loginUserResponse.email)
            putStringSet(KEY_ROLES, loginUserResponse.roles.toSet())
            apply()
        }
    }

    private fun getUserFromPreferences(): LoginUserResponse? {
        val accessToken = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
        val refreshToken = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        val userId = sharedPreferences.getString(KEY_USERID, null)?.toInt()
        val userName = sharedPreferences.getString(KEY_USERNAME, null)
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        val roles = sharedPreferences.getStringSet(KEY_ROLES, null)?.toList()

        return if (accessToken != null && refreshToken != null && userId != null && userName != null && email != null && roles != null) {
            LoginUserResponse(userId.toLong(), userName, email, accessToken, refreshToken, roles)
        } else {
            null
        }
    }
}