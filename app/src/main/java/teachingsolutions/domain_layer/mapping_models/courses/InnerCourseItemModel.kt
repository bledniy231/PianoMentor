package teachingsolutions.domain_layer.mapping_models.courses

class InnerCourseItemModel(
    _position: Int,
    _title: String,
    _courseItemType: CourseItemType,
    _courseItemProgressType: CourseItemProgressType,
    _subtitle: String? = null,
    _description: String? = null,
    _progressInPercent: Int = 0
) : CourseModel(_position, _title, _subtitle, _description, _progressInPercent) {
    var courseItemType: CourseItemType = _courseItemType
    var courseItemProgressType: CourseItemProgressType = _courseItemProgressType
}