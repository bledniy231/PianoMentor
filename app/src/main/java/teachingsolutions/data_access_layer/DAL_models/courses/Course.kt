package teachingsolutions.data_access_layer.DAL_models.courses

data class Course(
    val courseId: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val progressInPercent: Int
)
