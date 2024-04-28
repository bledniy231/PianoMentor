package teachingsolutions.domain_layer.mapping_models.courses

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class CourseModel(
    val courseId: Int,
    val position: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val progressInPercent: Int = 0
): IItemUIModel