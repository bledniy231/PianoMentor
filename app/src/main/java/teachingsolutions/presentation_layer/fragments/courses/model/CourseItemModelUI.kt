package teachingsolutions.presentation_layer.fragments.courses.model

import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemType

data class CourseItemModelUI(
    val title: String,
    val courseItemType: CourseItemType,
    val courseItemProgressType: CourseItemProgressType
)
