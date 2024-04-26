package teachingsolutions.data_access_layer.courses

import teachingsolutions.data_access_layer.DAL_models.courses.CourseItemsResponse
import teachingsolutions.data_access_layer.DAL_models.courses.CoursesResponse
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import teachingsolutions.data_access_layer.common.ActionResult
import java.io.IOException
import javax.inject.Inject

class CoursesDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {

    suspend fun getCourses(userId: Long): ActionResult<CoursesResponse> {
        var result: ActionResult<CoursesResponse>? = null
        result = try {
            val callResult = apiService.getCourses(userId)
            ActionResult.Success(callResult)
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error while getting courses, response not successful, ${e.message}"))
        }

        return result!!
    }

    suspend fun getCourseItems(userId: Long, courseId: Int): ActionResult<CourseItemsResponse> {
        var result: ActionResult<CourseItemsResponse>? = null
        result = try {
            val callResult = apiService.getCourseItems(userId, courseId)
            ActionResult.Success(callResult)
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error while getting course items of course: ${courseId}, response not successful, ${e.message}"))
        }

        return result!!
    }
}