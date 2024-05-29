package teachingsolutions.data_access_layer.DAL_models.exercise

data class ExerciseTask(
    val exerciseTaskId: Int,
    val courseItemId: Int,
    val exerciseTypeName: String,
    val intervalsInTaskNames: List<String>
)