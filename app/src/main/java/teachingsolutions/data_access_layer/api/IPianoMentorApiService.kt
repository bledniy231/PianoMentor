package teachingsolutions.data_access_layer.api

import okhttp3.OkHttpClient
import retrofit2.Call
import teachingsolutions.data_access_layer.DAL_models.LoginUserRequest
import teachingsolutions.data_access_layer.DAL_models.LoginUserResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import teachingsolutions.data_access_layer.DAL_models.RegisterUserRequest

interface IPianoMentorApiService {
    @POST("api/ApplicationUser/Login")
    suspend fun loginUser(@Body request: LoginUserRequest): LoginUserResponse

    @POST("api/ApplicationUser/Register")
    suspend fun registerUser(@Body request: RegisterUserRequest): LoginUserResponse

    @POST("api/ApplicationUser/Logout")
    suspend fun logoutUser(): Void
}