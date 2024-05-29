package teachingsolutions.presentation_layer.fragments.statistics.model

import teachingsolutions.domain_layer.domain_models.statistics.UserStatisticsModel

data class StatisticsResultUI(
    val success: UserStatisticsModel? = null,
    val error: String? = null
)