package teachingsolutions.presentation_layer.fragments.piano.model

import teachingsolutions.domain_layer.domain_models.exercise.Intervals

data class ExerciseTaskUI(
    val taskId: Int,
    val taskDescription: String,
    val intervalsInTask: List<Intervals>
)