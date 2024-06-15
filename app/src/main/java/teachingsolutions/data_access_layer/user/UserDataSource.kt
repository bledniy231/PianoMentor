package teachingsolutions.data_access_layer.user

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequestApi
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequestApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequestApi
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun login(request: LoginUserRequestApi): ActionResult<LoginUserResponseApi> {
        return try {val callResult = apiService.loginUser(request)
            when (callResult.failedMessage) {
                null -> {
                    ActionResult.Success(callResult)
                }
                else -> {
                    ActionResult.NormalError(callResult)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException(/*"Error login in, response not successful, ${*/e.message))
        }
    }

    suspend fun register(request: RegisterUserRequestApi): ActionResult<LoginUserResponseApi> {
        return try {
            val callResult = apiService.registerUser(request)
            when (callResult.failedMessage) {
                null -> {
                    ActionResult.Success(callResult)
                }
                else -> {
                    ActionResult.NormalError(callResult)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException(e.message))
        }
    }

    suspend fun logout(): ActionResult<Unit> {
        return try {
            apiService.logoutUser()
            ActionResult.Success(Unit)
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error logout in, response not successful"))
        }
    }

    suspend fun refreshUserTokens(request: RefreshUserTokensRequestApi): ActionResult<RefreshUserTokensResponseApi> {
        return try {
            val result = apiService.refreshUserTokens(request)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error refresh user tokens, response not successful"))
        }
    }

    suspend fun getProfilePhoto(userId: Long): ActionResult<ResponseBody> {
        return try {
            val response = apiService.getProfilePhoto(userId)
            if (response.isSuccessful && response.body() != null) {
                ActionResult.Success(response.body()!!)
            } else if (response.isSuccessful && response.body() == null) {
                ActionResult.Success(ByteArray(0).toResponseBody(null))
            } else {
                ActionResult.NormalError(response.body()?.string()?.toResponseBody() ?: "Ошибка в процессе получения фото профиля".toResponseBody())
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("${e.message}"))
        }
    }

    suspend fun setProfilePhoto(userId: Long, body: MultipartBody.Part): ActionResult<DefaultResponseApi> {
        return try {
            val response = apiService.updateProfilePhoto(userId, body)
            if (response.errors.isNullOrEmpty()) {
                ActionResult.Success(DefaultResponseApi())
            } else {
                ActionResult.NormalError(DefaultResponseApi(response.errors))
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("${e.message}"))
        }
    }
}