package teachingsolutions.presentation_layer.fragments.courses.model

data class CoursesResultUI(
    val success: List<CourseModelUI>? = null,
    val error: String? = null
)