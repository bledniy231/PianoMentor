package teachingsolutions.presentation_layer.fragments.courses.model

data class CourseItemsResult(
    val success: List<CourseItemModelUI>? = null,
    val error: String? = null
)