package teachingsolutions.data_access_layer.DAL_models.courses

data class CourseItem(
    val lessonId: Int,
    val title: String,
    val courseId: Int,
    val lessonNumber: Int,
    val isCompleted: Boolean,
    val lessonType: Int
)
