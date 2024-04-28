package teachingsolutions.data_access_layer.DAL_models.courses

data class CourseItem(
    val courseItemId: Int,
    val position: Int,
    val title: String,
    val courseItemType: String,
    val courseItemProgressType: String,
    val courseId: Int,
)
