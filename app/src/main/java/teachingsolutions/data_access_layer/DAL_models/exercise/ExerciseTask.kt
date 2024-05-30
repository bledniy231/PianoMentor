package teachingsolutions.data_access_layer.DAL_models.exercise

data class ExerciseTask(
    val exerciseTaskId: Int,
    val courseItemId: Int,
    val exerciseTypeId: Int,
    val intervalsInTaskIds: List<Int>
)