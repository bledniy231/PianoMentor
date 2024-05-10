package teachingsolutions.data_access_layer.DAL_models.quiz

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class GetQuizResponseApi (
    val questionModels: Array<QuizQuestion>? = null,
    val errors: Array<String>? = null
): DefaultResponseApi(errors)