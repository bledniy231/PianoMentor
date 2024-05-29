package teachingsolutions.data_access_layer.DAL_models.statistics

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class GetUserStatisticsResponseApi(
    val courseStatistics: BaseStatisticsModelApi,
    val lectureStatistics: BaseStatisticsModelApi,
    val exerciseStatistics: BaseStatisticsModelApi,
    val quizStatistics: BaseStatisticsModelApi,
    val viewPagerTexts: Array<ViewPagerText?>,
    errors: Array<String>? = null
) : DefaultResponseApi(errors)
