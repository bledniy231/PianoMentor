package teachingsolutions.presentation_layer.fragments.statistics.model

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class StatisticsViewPagerItemModelUI(
    var progressValueAbsolute: Int,
    var progressValueInPercent: Int,
    var titleText: String,
    var descriptionText: String
) : IItemUIModel