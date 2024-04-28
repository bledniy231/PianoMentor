package teachingsolutions.data_access_layer.courses

import teachingsolutions.data_access_layer.DAL_models.courses.CourseItemsResponse
import teachingsolutions.data_access_layer.DAL_models.courses.CoursesResponse
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import teachingsolutions.data_access_layer.common.ActionResult
import java.io.IOException
import javax.inject.Inject

class CoursesDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun getCourses(userId: Long): ActionResult<CoursesResponse> {
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

    suspend fun getCourseItems(userId: Long, courseId: Int): ActionResult<CourseItemsResponse> {
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
}