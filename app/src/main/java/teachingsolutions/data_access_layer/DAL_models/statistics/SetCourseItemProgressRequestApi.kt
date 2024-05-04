package teachingsolutions.data_access_layer.DAL_models.statistics

data class SetCourseItemProgressRequestApi(
    var userId: Long,
    var courseId: Int,
    var courseItemId: Int,
    var courseItemProgressType: String
)
