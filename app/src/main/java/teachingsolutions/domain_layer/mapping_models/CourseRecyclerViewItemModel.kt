package teachingsolutions.domain_layer.mapping_models

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class CourseRecyclerViewItemModel(
    val title: String,
    val subtitle: String,
    val description: String,
    val progressInPercent: Int
): IItemUIModel