package teachingsolutions.presentation_layer.fragments.data.statistics.model

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class StatisticsViewPagerItemModel(
    var progressValueAbsolute: Int,
    var progressValueInPercent: Int,
    var titleText: String,
    var descriptionText: String
) : IItemUIModel