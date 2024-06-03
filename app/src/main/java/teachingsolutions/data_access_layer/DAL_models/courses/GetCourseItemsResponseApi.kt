package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class GetCourseItemsResponseApi(
    val courseItems: ArrayList<CourseItem>,
    errors: Array<String>?
): DefaultResponseApi(errors)