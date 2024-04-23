package teachingsolutions.data_access_layer.DAL_models.courses

import teachingsolutions.data_access_layer.common.LessonTypes

data class CourseLessonItem(
    val lessonId: Int,
    val title: String,
    val courseId: Int,
    val lessonNumber: Int,
    val isCompleted: Boolean,
    val lessonType: Int
)
