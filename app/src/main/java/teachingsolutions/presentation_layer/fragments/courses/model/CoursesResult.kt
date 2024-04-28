package teachingsolutions.presentation_layer.fragments.courses.model

data class CoursesResult(
    val success: List<CourseModelUI>? = null,
    val error: String? = null
)