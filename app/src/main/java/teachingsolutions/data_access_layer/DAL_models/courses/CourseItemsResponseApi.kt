package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class CourseItemsResponseApi(
    val courseItems: ArrayList<CourseItem>,
    val errors: Array<String>?
): DefaultResponseApi(errors)