package teachingsolutions.presentation_layer.fragments.data.courses.model

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class CourseRecyclerViewItemModel(
    public val title: String,
    public val subtitle: String,
    public val description: String,
    public val progressInPercent: Int
): IItemUIModel