package teachingsolutions.data_access_layer.exercise

import teachingsolutions.data_access_layer.DAL_models.exercise.GetExerciseTaskResponseApi
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import teachingsolutions.data_access_layer.common.ActionResult
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun getExerciseTask(courseItemId: Int): ActionResult<GetExerciseTaskResponseApi> {
        return try {
            val result = apiService.getExerciseTask(courseItemId)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("${e.message}"))
        }
    }
}