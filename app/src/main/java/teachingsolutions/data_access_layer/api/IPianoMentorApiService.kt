package teachingsolutions.data_access_layer.api

import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequest
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequest
import teachingsolutions.data_access_layer.DAL_models.courses.CourseItemsResponse
import teachingsolutions.data_access_layer.DAL_models.courses.CoursesResponse
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequest
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponse

interface IPianoMentorApiService {
    @POST("api/ApplicationUser/Login")
    suspend fun loginUser(@Body request: LoginUserRequest): LoginUserResponse

    @POST("api/ApplicationUser/Register")
    suspend fun registerUser(@Body request: RegisterUserRequest): LoginUserResponse

    @POST("api/ApplicationUser/Logout")
    suspend fun logoutUser(): Void

    @POST("api/ApplicationUser/RefreshUserTokens")
    suspend fun refreshUserTokens(@Body request: RefreshUserTokensRequest): RefreshUserTokensResponse

    @GET("api/Courses/GetCourses")
    suspend fun getCourses(@Query("userId") userId: Long): CoursesResponse

    @GET("api/Courses/GetCourseItems")
    suspend fun getCourseItems(@Query("userId") userId: Long, @Query("courseId") courseId: Int): CourseItemsResponse
}