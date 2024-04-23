package teachingsolutions.data_access_layer.login_registration

import retrofit2.Response
import teachingsolutions.data_access_layer.DAL_models.LoginUserRequest
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.DAL_models.LoginUserResponse
import teachingsolutions.data_access_layer.DAL_models.RegisterUserRequest
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import java.io.IOException
import javax.inject.Inject


class UserDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun login(request: LoginUserRequest): ActionResult<LoginUserResponse> {
        var result: ActionResult<LoginUserResponse>? = null
        try {
            val callResult = apiService.loginUser(request)
            result = ActionResult.Success(callResult)
        } catch (e: Exception) {
            result = ActionResult.Error(IOException("Error login in, response not successful, ${e.message}"))
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

        return result!!
    }

    suspend fun register(request: RegisterUserRequest): ActionResult<LoginUserResponse> {
        var result: ActionResult<LoginUserResponse>? = null
        try {
            val callResult = apiService.registerUser(request)
            result = ActionResult.Success(callResult)
        } catch (e: Exception) {
            result = ActionResult.Error(IOException("Error register in, response not successful"))
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

        return result!!
    }

    suspend fun logout(): ActionResult<Unit> {
        var result: ActionResult<Unit>? = null
        try {
            apiService.logoutUser()
            result = ActionResult.Success(Unit)
        } catch (e: Exception) {
            result = ActionResult.Error(IOException("Error logout in, response not successful"))
        }

        return result!!
    }

    private fun loginUserOnResponse(response: Response<LoginUserResponse>): ActionResult<LoginUserResponse> {
        return if (response.isSuccessful) {
            val user = response.body()
            if (user != null && user.failedMessage.isNullOrEmpty()) {
                ActionResult.Success(user)
            } else {
                ActionResult.Error(IOException("Error login in, response failed message: ${user?.failedMessage ?: "cannot map response to user model"}"))
            }
        } else {
            ActionResult.Error(IOException("Error login in, response not successful"))
        }
    }
}