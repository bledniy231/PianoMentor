package teachingsolutions.data_access_layer.DAL_models.statistics

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

data class GetUserStatisticsResponseApi(
    var courseStatistics: BaseStatisticsModelApi,
    var lectureStatistics: BaseStatisticsModelApi,
    var exerciseStatistics: BaseStatisticsModelApi,
    var quizStatistics: BaseStatisticsModelApi,
    var viewPagerTexts: Array<ViewPagerText?>,
    var errors: Array<String>?
) : DefaultResponseApi(errors)
