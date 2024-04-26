package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponse

class CourseItemsResponse(
    val courseItems: ArrayList<CourseItem>,
    val errors: Array<String>?
): DefaultResponse(errors)