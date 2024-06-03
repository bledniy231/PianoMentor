package teachingsolutions.data_access_layer.user

import retrofit2.Response
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

    private fun loginUserOnResponse(response: Response<LoginUserResponseApi>): ActionResult<LoginUserResponseApi> {
        return if (response.isSuccessful) {
            val user = response.body()
            if (user != null && user.failedMessage.isNullOrEmpty()) {
                ActionResult.Success(user)
            } else {
                ActionResult.ExceptionError(IOException("Error login in, response failed message: ${user?.failedMessage ?: "cannot map response to user model"}"))
            }
        } else {
            ActionResult.ExceptionError(IOException("Error login in, response not successful"))
        }
    }
}