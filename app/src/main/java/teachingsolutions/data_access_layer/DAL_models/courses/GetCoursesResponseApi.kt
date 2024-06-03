package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class GetCoursesResponseApi (
    val courses: ArrayList<Course>,
    errors: Array<String>? = null
): DefaultResponseApi(errors)