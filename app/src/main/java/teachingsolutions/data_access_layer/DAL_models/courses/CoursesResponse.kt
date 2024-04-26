package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponse

class CoursesResponse (
    val courses: ArrayList<Course>,
    val errors: Array<String>?
): DefaultResponse(errors)