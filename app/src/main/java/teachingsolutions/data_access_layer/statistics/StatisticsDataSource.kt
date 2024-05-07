package teachingsolutions.data_access_layer.statistics

import okio.IOException
import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi
import teachingsolutions.data_access_layer.DAL_models.statistics.BaseStatisticsModelApi
import teachingsolutions.data_access_layer.DAL_models.statistics.GetUserStatisticsResponseApi
import teachingsolutions.data_access_layer.DAL_models.statistics.SetCourseItemProgressRequestApi
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import teachingsolutions.data_access_layer.common.ActionResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {

    suspend fun getUserStatistics(userId: Long): ActionResult<GetUserStatisticsResponseApi> {
        return try {
            val response = apiService.getUserStatistics(userId)
            if (response.code() == 401) {
                return ActionResult.ExceptionError(IOException("Unauthorized"))
            }
            val apiResult = response.body() ?: return ActionResult.ExceptionError(IOException("Empty response"))
            when (apiResult.errors) {
                null -> {
                    ActionResult.Success(apiResult)
                }
                else -> {
                    ActionResult.NormalError(apiResult)
                }
            }
        }
        catch (e: Exception) {
            ActionResult.ExceptionError(e)
        }
    }

    suspend fun setCourseItemProgress(userId: Long, courseId: Int, courseItemId: Int, courseItemProgressType: String): ActionResult<DefaultResponseApi> {
        return try {
            val request = SetCourseItemProgressRequestApi(userId, courseId, courseItemId, courseItemProgressType)
            val apiResult = apiService.setCourseItemProgress(request)
            when (apiResult._errors) {
                null -> {
                    ActionResult.Success(apiResult)
                }
                else -> {
                    ActionResult.NormalError(apiResult)
                }
            }
        }
        catch (e: Exception) {
            ActionResult.ExceptionError(e)
        }
    }
}