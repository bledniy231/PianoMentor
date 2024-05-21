package teachingsolutions.domain_layer.mapping_models.courses

data class CourseModel(
    val courseId: Int,
    val position: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    var progressInPercent: Int = 0
)