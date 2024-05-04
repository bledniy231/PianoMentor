package teachingsolutions.presentation_layer.fragments.statistics.model

import teachingsolutions.domain_layer.mapping_models.statistics.BaseStatisticsModel
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel

data class StatisticsResultUI(
    val success: UserStatisticsModel? = null,
    val error: String? = null
)