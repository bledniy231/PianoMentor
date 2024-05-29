package teachingsolutions.domain_layer.domain_models.courses

data class CourseItemModel(
    val courseItemId: Int,
    val position: Int,
    val title: String,
    val courseItemType: CourseItemType,
    var courseItemProgressType: CourseItemProgressType,
    val courseId: Int
)