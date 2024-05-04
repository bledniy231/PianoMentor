package teachingsolutions.data_access_layer.DAL_models.statistics

data class CourseUserProgressModelApi(
    var userId: Long,
    var courseId: Int,
    var courseName: String,
    var progressInPercent: Int
)
