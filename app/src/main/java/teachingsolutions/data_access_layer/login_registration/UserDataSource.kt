package teachingsolutions.data_access_layer.login_registration

import retrofit2.Response
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequest
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponse
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequest
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponse
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequest
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import java.io.IOException
import javax.inject.Inject


class UserDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun login(request: LoginUserRequest): ActionResult<LoginUserResponse> {
        return try {
            val callResult = apiService.loginUser(request)
            when (callResult.failedMessage) {
                null -> {
                    ActionResult.Success(callResult)
                }
                else -> {
                    ActionResult.NormalError(callResult)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error login in, response not successful, ${e.message}"))
        }

//        callLogin.enqueue(object: Callback<LoginUserResponse> {
//            override fun onResponse(call: Call<LoginUserResponse>, response: Response<LoginUserResponse>) {
//                result = loginUserOnResponse(response)
//            }
//
//            override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
//                result = ActionResult.Error(IOException("Error login in, exception caught", t))
//            }
//        })
    }

    suspend fun register(request: RegisterUserRequest): ActionResult<LoginUserResponse> {
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
            ActionResult.ExceptionError(IOException("Error register in, response not successful"))
        }
//        callRegister.enqueue(object: Callback<LoginUserResponse> {
//            override fun onResponse(call: Call<LoginUserResponse>, response: Response<LoginUserResponse>) {
//                result = loginUserOnResponse(response)
//            }
//
//            override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
//                result = ActionResult.Error(IOException("Error login in, exception caught", t))
//            }
//        })
    }

    suspend fun logout(): ActionResult<Unit> {
        return try {
            apiService.logoutUser()
            ActionResult.Success(Unit)
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error logout in, response not successful"))
        }
    }

    suspend fun refreshUserTokens(request: RefreshUserTokensRequest): ActionResult<RefreshUserTokensResponse> {
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

    private fun loginUserOnResponse(response: Response<LoginUserResponse>): ActionResult<LoginUserResponse> {
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