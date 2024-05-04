package teachingsolutions.data_access_layer.DAL_models.statistics

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

data class GetUserStatisticsResponseApi(
    var coursesUsersProgress: List<CourseUserProgressModelApi>,
    var completedLectures: Int,
    var allLectures: Int,
    var completedExercises: Int,
    var allExercises: Int,
    var completedQuizzes: Int,
    var allQuizzes: Int,
    var errors: Array<String>?
) : DefaultResponseApi(errors)
