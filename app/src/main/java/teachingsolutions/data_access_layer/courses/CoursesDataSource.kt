package teachingsolutions.data_access_layer.courses

import okhttp3.ResponseBody
import teachingsolutions.data_access_layer.DAL_models.courses.GetCourseItemsResponseApi
import teachingsolutions.data_access_layer.DAL_models.courses.GetCoursesResponseApi
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.domain_layer.domain_models.courses.CourseItemType
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun getCourses(userId: Long): ActionResult<GetCoursesResponseApi> {
        return try {
            val result = apiService.getCourses(userId)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error while getting courses, response not successful, ${e.message}"))
        }
    }

    suspend fun getCourseItems(userId: Long, courseId: Int): ActionResult<GetCourseItemsResponseApi> {
        return try {
            val result = apiService.getCourseItems(userId, courseId)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error while getting course items of course: ${courseId}, response not successful, ${e.message}"))
        }
    }

    suspend fun getCourseItemsWithFilter(userId: Long, filter: CourseItemType): ActionResult<GetCourseItemsResponseApi> {
        return try {
            val result = apiService.getCourseItemsWithFilter(userId, filter.value)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error while getting course items with filter: ${filter}, response not successful, ${e.message}"))
        }
    }

    suspend fun getCourseItemFile(courseItemId: Int): ActionResult<ResponseBody> {
        val response = apiService.getCourseItemFile(courseItemId)
        return if (response.isSuccessful) {
            ActionResult.Success(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string() ?: response.body()?.string()
            ActionResult.ExceptionError(IOException("$errorBody"))
        }
    }
}