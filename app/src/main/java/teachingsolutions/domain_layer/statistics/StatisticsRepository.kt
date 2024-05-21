package teachingsolutions.domain_layer.statistics

import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.statistics.StatisticsDataSource
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.mapping_models.statistics.BaseStatisticsModel
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsResultUI
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsViewPagerItemModelUI
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
                val userStatModel = UserStatisticsModel(
                    result.data.viewPagerTexts.map {
                        StatisticsViewPagerItemModelUI(
                            it?.progressValueAbsolute ?: 0,
                            it?.progressValueInPercent ?: 0,
                            it?.title ?: "",
                            it?.description ?: "",
                        )
                    },
                    BaseStatisticsModel(
                        result.data.exerciseStatistics.progressValueAbsolute,
                        result.data.exerciseStatistics.progressValueInPercent,
                        result.data.exerciseStatistics.title
                    ),
                    BaseStatisticsModel(
                        result.data.lectureStatistics.progressValueAbsolute,
                        result.data.lectureStatistics.progressValueInPercent,
                        result.data.lectureStatistics.title
                    ),
                    BaseStatisticsModel(
                        result.data.courseStatistics.progressValueAbsolute,
                        result.data.courseStatistics.progressValueInPercent,
                        result.data.courseStatistics.title
                    )
                )

                cachedStatistics = userStatModel

                return StatisticsResultUI(cachedStatistics!!)
            }
            is ActionResult.NormalError -> {
                return StatisticsResultUI(null, result.data.errors?.joinToString { it } ?: "Error while getting statistics")
            }
            is ActionResult.ExceptionError -> {
                return if (result.exception.message == "Unauthorized") {
                    StatisticsResultUI(null, "Unauthorized")
                } else {
                    StatisticsResultUI(null, result.exception.message ?: "Exception while getting statistics")
                }
            }
        }
    }

    suspend fun setCourseItemProgress(userId: Long, courseId: Int, courseItemId: Int, progress: CourseItemProgressType): DefaultResponseUI {
        cachedStatistics = null
        return when (val result = statisticsDataSource.setCourseItemProgress(userId, courseId, courseItemId, progress.value)) {
            is ActionResult.Success -> {
                DefaultResponseUI()
            }
            is ActionResult.NormalError -> {
                DefaultResponseUI(result.data._errors?.joinToString { it } ?: "Error while setting course item progress")
            }
            is ActionResult.ExceptionError -> {
                DefaultResponseUI(result.exception.message ?: "Exception while setting course item progress")
            }
        }
    }

//    fun addCompletedQuizToStatistics(newValueInPercent: Int) {
//        cachedStatistics?.statListViewPagerItems?.find { it.titleText.contains("тест", true) }?.let {
//            it.progressValueInPercent = newValueInPercent
//        }
//    }
//
//    fun addCompletedLectureToStatistics(newValueInPercent: Int) {
//        cachedStatistics?.lecturesProgressModel?.let {
//            it.progressValueInPercent = newValueInPercent
//        }
//    }
//
//    fun addCompletedExerciseInStatistics(newValueInPercent: Int) {
//        cachedStatistics?.exercisesProgressModel?.let {
//            it.progressValueInPercent = newValueInPercent
//        }
//    }
//
    fun clearCache() {
        cachedStatistics = null
    }
}