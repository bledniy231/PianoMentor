package teachingsolutions.domain_layer.domain_models.courses

import java.util.Locale

enum class CourseItemType(val value: String) {
    LECTURE("Lecture"),
    EXERCISE("Exercise"),
    QUIZ("Quiz");
    companion object {
        fun from(type: String?): CourseItemType = entries.find { it.value.lowercase() == type?.lowercase(Locale.ROOT) } ?: LECTURE
    }
}