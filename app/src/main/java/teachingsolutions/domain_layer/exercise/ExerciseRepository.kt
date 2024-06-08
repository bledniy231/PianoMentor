package teachingsolutions.domain_layer.exercise

import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.exercise.ExerciseDataSource
import teachingsolutions.domain_layer.common.TaskDescriptionManager
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTaskModel
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTypes
import teachingsolutions.domain_layer.domain_models.exercise.Intervals
import teachingsolutions.domain_layer.statistics.StatisticsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDataSource: ExerciseDataSource,
    private val statisticsRepository: StatisticsRepository,
    private val taskDescriptionManager: TaskDescriptionManager) {

    private var exerciseTasksCached: MutableList<ExerciseTaskModel> = mutableListOf()

    suspend fun getExerciseTask(courseItemId: Int): Pair<ExerciseTaskModel?, String?> {
        val fromCache = exerciseTasksCached.find { it.courseItemId == courseItemId }
        if (fromCache != null) {
            return Pair(fromCache, null)
        }

        return when (val result = exerciseDataSource.getExerciseTask(courseItemId)) {
            is ActionResult.Success -> {
                val exerciseType = ExerciseTypes.from(result.data.exerciseTask.exerciseTypeId)
                val exerciseTaskModel = ExerciseTaskModel(
                    result.data.exerciseTask.exerciseTaskId,
                    result.data.exerciseTask.courseItemId,
                    taskDescriptionManager.getDescription(exerciseType),
                    exerciseType,
                    result.data.exerciseTask.intervalsInTaskIds.map { Intervals.from(it) }.sortedBy { it.ordinal }
                )

                exerciseTasksCached.add(exerciseTaskModel)

                Pair(exerciseTaskModel, null)
            }
            is ActionResult.NormalError -> {
                Pair(null, result.data.errors?.joinToString { it } ?: "Error while getting exercise task")
            }
            is ActionResult.ExceptionError -> {
                Pair(null, result.exception.message ?: "Error while getting exercise task")
            }
        }
    }
}