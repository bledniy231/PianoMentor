package teachingsolutions.domain_layer.mapping_models.statistics

import teachingsolutions.presentation_layer.fragments.statistics.StatisticsViewPagerItemModelUI

data class UserStatisticsModel(
    var statListViewPagerItems: List<StatisticsViewPagerItemModelUI>,
    var exercisesProgressModel: ExercisesProgressModel,
    var lecturesProgressModel: LecturesProgressModel,
    var coursesProgressModel: CoursesProgressModel
)