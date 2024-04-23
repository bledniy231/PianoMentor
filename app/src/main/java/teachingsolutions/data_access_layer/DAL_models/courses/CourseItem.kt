package teachingsolutions.data_access_layer.DAL_models.courses

data class CourseItem(
    val courseId: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val progressInPercent: Int
)
