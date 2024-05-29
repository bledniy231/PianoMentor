package teachingsolutions.presentation_layer.fragments.courses.model

import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.domain_models.courses.CourseItemType

data class CourseItemModelUI(
    override val courseId: Int,
    override val title: String,
    val courseItemType: CourseItemType,
    val courseItemProgressType: CourseItemProgressType,
    val courseItemId: Int,
    override val isExactItem: Boolean = true,
    override val subtitle: String = "",
    override val description: String = "",
    override val progressInPercent: Int = 0
): CourseModelUI(courseId, title, subtitle, description, progressInPercent, isExactItem)