package teachingsolutions.domain_layer.statistics

import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.statistics.StatisticsDataSource
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsResultUI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(private val statisticsDataSource: StatisticsDataSource) {

    private var cachedStatistics: UserStatisticsModel? = null

    suspend fun getUserStatistics(userId: Long): StatisticsResultUI {
        if (cachedStatistics != null) {
            return StatisticsResultUI(cachedStatistics!!)
        }

        when (val result = statisticsDataSource.getUserStatistics(userId)) {
            is ActionResult.Success -> {

                return cachedStatistics
            }
            is ActionResult.NormalError -> {
                return StatisticsResultUI(null, result.data.errors?.joinToString { it } ?: "Error while getting statistics")
            }
            is ActionResult.ExceptionError -> {
                return StatisticsResultUI(null, result.exception.message ?: "Exception while getting statistics")
            }
            else -> return ActionResult.ExceptionError(Exception("OTHER_EX: No courses found or no internet connection"))
        }
    }
}