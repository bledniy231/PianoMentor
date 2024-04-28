package teachingsolutions.domain_layer.mapping_models.courses

data class CourseItemModel(
    val courseItemId: Int,
    val position: Int,
    val title: String,
    val courseItemType: CourseItemType,
    val courseItemProgressType: CourseItemProgressType,
    val courseId: Int
)