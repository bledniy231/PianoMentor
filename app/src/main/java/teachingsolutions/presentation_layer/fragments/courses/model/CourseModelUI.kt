package teachingsolutions.presentation_layer.fragments.courses.model

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

open class CourseModelUI(
    open val courseId: Int,
    open val title: String,
    open val subtitle: String = "",
    open val description: String = "",
    open val progressInPercent: Int = 0,
    open val isExactItem: Boolean = false
): IItemUIModel