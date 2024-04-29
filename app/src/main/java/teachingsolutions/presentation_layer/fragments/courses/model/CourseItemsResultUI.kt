package teachingsolutions.presentation_layer.fragments.courses.model

data class CourseItemsResultUI(
    val success: List<CourseItemModelUI>? = null,
    val error: String? = null
)