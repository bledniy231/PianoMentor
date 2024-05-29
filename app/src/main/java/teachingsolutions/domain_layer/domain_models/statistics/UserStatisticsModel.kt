package teachingsolutions.domain_layer.domain_models.statistics

import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsViewPagerItemModelUI

data class UserStatisticsModel(
    var statListViewPagerItems: List<StatisticsViewPagerItemModelUI>,
    var exercisesProgressModel: BaseStatisticsModel,
    var lecturesProgressModel: BaseStatisticsModel,
    var coursesProgressModel: BaseStatisticsModel
)