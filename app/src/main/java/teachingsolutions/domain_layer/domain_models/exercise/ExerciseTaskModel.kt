package teachingsolutions.domain_layer.domain_models.exercise

class ExerciseTaskModel(
    val exerciseTaskId: Int,
    val courseItemId: Int,
    val taskDescription: String,
    val exerciseType: ExerciseTypes,
    val intervalsInTask: List<Intervals>
)