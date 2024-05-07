package teachingsolutions.domain_layer.mapping_models.courses

enum class CourseItemProgressType(val value: String) {
    NOT_STARTED("NotStarted"),
    IN_PROGRESS("InProgress"),
    COMPLETED("Completed"),
    FAILED("Failed");

    companion object {
        fun from(type: String?): CourseItemProgressType = entries.find { it.value.lowercase() == type?.lowercase() } ?: NOT_STARTED
    }
}