package teachingsolutions.domain_layer.user

import android.content.SharedPreferences
import okhttp3.MultipartBody
import teachingsolutions.data_access_layer.DAL_models.user.JwtTokens
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequestApi
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequestApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequestApi
import teachingsolutions.data_access_layer.user.UserDataSource
import teachingsolutions.data_access_layer.shared_preferences_keys.SharedPreferencesKeys
import teachingsolutions.domain_layer.common.CustomGsonSupplier
import teachingsolutions.domain_layer.common.FileStorageManager
import teachingsolutions.domain_layer.courses.CoursesRepository
import teachingsolutions.domain_layer.statistics.StatisticsRepository
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.common.FileResultUI
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val prefKeys: SharedPreferencesKeys,
    private val dataSource: UserDataSource,
    customGsonSupplier: CustomGsonSupplier,
    private val coursesRepository: CoursesRepository,
    private val statisticsRepository: StatisticsRepository,
    private val fileStorageManager: FileStorageManager
) {

    private var user: LoginUserResponseApi? = null
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

    suspend fun logout(isAccessTokenExpired: Boolean): Boolean {
        if (!isAccessTokenExpired) {
            dataSource.logout()
        }

        coursesRepository.clearCache()
        statisticsRepository.clearCache()
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

        return true
    }

    suspend fun login(username: String, password: String): ActionResult<LoginUserResponseApi> {
        val result = dataSource.login(LoginUserRequestApi(username, password))

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data, true)
        }

        return result
    }

    suspend fun register(username: String, email: String, password: String, confirmPassword: String): ActionResult<LoginUserResponseApi> {
        val result = dataSource.register(RegisterUserRequestApi(username, email, password, confirmPassword, listOf("user")))

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data, true)
        }

        return result
    }

    private suspend fun refreshUserTokens(jwtTokens: JwtTokens): ActionResult<RefreshUserTokensResponseApi> {
        return dataSource.refreshUserTokens(RefreshUserTokensRequestApi(jwtTokens))
    }

    private fun setLoggedInUser(loginUserResponseApi: LoginUserResponseApi, addToSharedPrefs: Boolean) {
        this.user = loginUserResponseApi

        if (addToSharedPrefs) {
            with(sharedPreferences.edit()) {
                putString(prefKeys.KEY_USER_TOKENS, gson.toJson(loginUserResponseApi.jwtTokensModel))
                putString(prefKeys.KEY_USERID, loginUserResponseApi.userId.toString())
                putString(prefKeys.KEY_USERNAME, loginUserResponseApi.userName)
                putString(prefKeys.KEY_EMAIL, loginUserResponseApi.email)
                putStringSet(prefKeys.KEY_ROLES, loginUserResponseApi.roles.toSet())
                apply()
            }
        }
    }

    suspend fun refreshUserIfNeeds(): Boolean {
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
                setLoggedInUser(LoginUserResponseApi(userId, userName, email, jwtTokens, roles), false)
                return true
            }
            else if (!isTokenExpired(jwtTokens.refreshTokenExpireTime))
            {
                val result = refreshUserTokens(jwtTokens)
                if (result is ActionResult.Success)
                {
                    result.data.jwtTokens?.let { newTokens ->
                        setLoggedInUser(LoginUserResponseApi(userId, userName, email, newTokens, roles), addToSharedPrefs = true)
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

    suspend fun getProfilePhoto(): FileResultUI {
        if (userId == null) {
            return FileResultUI(null, null)
        }

        val tempPhoto = fileStorageManager.getTempProfilePhotoFile()
        if (tempPhoto != null) {
            return FileResultUI(tempPhoto, null)
        }

        val result = dataSource.getProfilePhoto(userId!!)
        return when {
            result is ActionResult.Success && result.data.contentLength() > 0 -> {
                val file = fileStorageManager.createTempProfilePhotoFile(result.data)
                FileResultUI(file, null)
            }
            result is ActionResult.Success && result.data.contentLength() == 0L -> {
                FileResultUI(null, null)
            }
            result is ActionResult.NormalError -> {
                FileResultUI(null, result.data.string())
            }
            result is ActionResult.ExceptionError -> {
                FileResultUI(null, result.exception.message)
            }
            else -> {
                FileResultUI(null, "Unknown error")
            }
        }
    }

    suspend fun setProfilePhoto(body: MultipartBody.Part): DefaultResponseUI {
        if (userId == null) {
            return DefaultResponseUI("Пользователь не найден")
        }

        return when (val result = dataSource.setProfilePhoto(userId!!, body)) {
            is ActionResult.Success -> {
                DefaultResponseUI("Фото было загружено")
            }
            is ActionResult.NormalError -> {
                DefaultResponseUI(result.data.errors?.joinToString { it } ?: "Неизвестная ошибка")
            }
            is ActionResult.ExceptionError -> {
                DefaultResponseUI(result.exception.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun isTokenExpired(tokenExpireTime: LocalDateTime?): Boolean {
        return tokenExpireTime?.isBefore(LocalDateTime.now(ZoneOffset.UTC)) ?: false
    }
}