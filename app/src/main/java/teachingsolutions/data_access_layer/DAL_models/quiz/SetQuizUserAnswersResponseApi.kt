package teachingsolutions.data_access_layer.DAL_models.quiz

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class SetQuizUserAnswersResponseApi(
    val progressType: String,
    errors: Array<String>? = null
) : DefaultResponseApi(errors)