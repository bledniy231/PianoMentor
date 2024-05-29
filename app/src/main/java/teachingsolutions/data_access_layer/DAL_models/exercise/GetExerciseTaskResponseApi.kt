package teachingsolutions.data_access_layer.DAL_models.exercise

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class GetExerciseTaskResponseApi(
    val exerciseTask: ExerciseTask,
    errors: Array<String>? = null
): DefaultResponseApi(errors)