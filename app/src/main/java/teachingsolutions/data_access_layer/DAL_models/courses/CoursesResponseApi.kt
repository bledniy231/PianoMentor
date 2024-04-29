package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class CoursesResponseApi (
    val courses: ArrayList<Course>,
    val errors: Array<String>?
): DefaultResponseApi(errors)