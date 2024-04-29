package teachingsolutions.data_access_layer.api

import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequest
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequestApi
import teachingsolutions.data_access_layer.DAL_models.courses.CourseItemsResponseApi
import teachingsolutions.data_access_layer.DAL_models.courses.CoursesResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequestApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponseApi

interface IPianoMentorApiService {
    @POST("api/ApplicationUser/Login")
    suspend fun loginUser(@Body request: LoginUserRequest): LoginUserResponse

    @POST("api/ApplicationUser/Register")
    suspend fun registerUser(@Body request: RegisterUserRequestApi): LoginUserResponse

    @POST("api/ApplicationUser/Logout")
    suspend fun logoutUser(): Void

    @POST("api/ApplicationUser/RefreshUserTokens")
    suspend fun refreshUserTokens(@Body request: RefreshUserTokensRequestApi): RefreshUserTokensResponseApi

    @GET("api/Courses/GetCourses")
    suspend fun getCourses(@Query("userId") userId: Long): CoursesResponseApi

    @GET("api/Courses/GetCourseItems")
    suspend fun getCourseItems(@Query("userId") userId: Long, @Query("courseId") courseId: Int): CourseItemsResponseApi
}