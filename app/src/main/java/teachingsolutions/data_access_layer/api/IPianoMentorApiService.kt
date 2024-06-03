package teachingsolutions.data_access_layer.api

import okhttp3.ResponseBody
import retrofit2.Response
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserRequestApi
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponseApi
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RegisterUserRequestApi
import teachingsolutions.data_access_layer.DAL_models.courses.GetCourseItemsResponseApi
import teachingsolutions.data_access_layer.DAL_models.courses.GetCoursesResponseApi
import teachingsolutions.data_access_layer.DAL_models.exercise.GetExerciseTaskResponseApi
import teachingsolutions.data_access_layer.DAL_models.quiz.GetQuizResponseApi
import teachingsolutions.data_access_layer.DAL_models.quiz.SetQuizUserAnswersRequestApi
import teachingsolutions.data_access_layer.DAL_models.quiz.SetQuizUserAnswersResponseApi
import teachingsolutions.data_access_layer.DAL_models.statistics.SetCourseItemProgressRequestApi
import teachingsolutions.data_access_layer.DAL_models.statistics.GetUserStatisticsResponseApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensRequestApi
import teachingsolutions.data_access_layer.DAL_models.user.RefreshUserTokensResponseApi

interface IPianoMentorApiService {
    @POST("api/ApplicationUser/Login")
    suspend fun loginUser(@Body request: LoginUserRequestApi): LoginUserResponseApi

    @POST("api/ApplicationUser/Register")
    suspend fun registerUser(@Body request: RegisterUserRequestApi): LoginUserResponseApi

    @POST("api/ApplicationUser/Logout")
    suspend fun logoutUser(): Void

    @POST("api/ApplicationUser/RefreshUserTokens")
    suspend fun refreshUserTokens(@Body request: RefreshUserTokensRequestApi): RefreshUserTokensResponseApi

    @GET("api/Courses/GetCourses")
    suspend fun getCourses(@Query("userId") userId: Long): GetCoursesResponseApi

    @GET("api/Courses/GetCourseItems")
    suspend fun getCourseItems(@Query("userId") userId: Long, @Query("courseId") courseId: Int): GetCourseItemsResponseApi

    @GET("api/Files/DownloadCourseItemFile")
    suspend fun getCourseItemFile(@Query("courseItemId") courseItemId: Int): Response<ResponseBody>

    @PUT("api/Courses/SetCourseItemProgress")
    suspend fun setCourseItemProgress(@Body request: SetCourseItemProgressRequestApi): DefaultResponseApi

    @GET("api/ApplicationUser/GetUserStatistics")
    suspend fun getUserStatistics(@Query("userId") userId: Long): Response<GetUserStatisticsResponseApi>

    @GET("api/Courses/GetQuiz")
    suspend fun getCourseItemQuiz(@Query("courseId") courseId: Int, @Query("courseItemId") courseItemId: Int, @Query("userId") userId: Long): GetQuizResponseApi

    @POST("api/Courses/SetQuizUserAnswers")
    suspend fun setCourseItemQuizUserAnswers(@Body request: SetQuizUserAnswersRequestApi): SetQuizUserAnswersResponseApi

    @GET("api/Files/DownloadQuizQuestionFile")
    suspend fun getQuizQuestionFile(@Query("dataSetId") dataSetId: Long): Response<ResponseBody>

    @GET("api/Courses/GetExerciseTask")
    suspend fun getExerciseTask(@Query("courseItemId") courseItemId: Int): GetExerciseTaskResponseApi
}