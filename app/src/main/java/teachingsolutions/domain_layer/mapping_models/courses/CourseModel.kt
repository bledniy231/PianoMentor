package teachingsolutions.domain_layer.mapping_models.courses

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

open class CourseModel(
    _position: Int,
    _title: String,
    _subtitle: String?,
    _description: String?,
    _progressInPercent: Int = 0
): IItemUIModel {
    var position: Int = _position
    var title: String = _title
    var subtitle: String? = _subtitle
    var description: String? = _description
    var progressInPercent: Int = _progressInPercent
}