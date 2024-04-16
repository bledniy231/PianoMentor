package teachingsolutions.presentation_layer.fragments.data.statistics.model

data class UserStatisticsModel(
    var statListViewPagerItems: List<StatisticsViewPagerItemModel>,
    var exercisesProgressModel: ExercisesProgressModel,
    var lecturesProgressModel: LecturesProgressModel,
    var coursesProgressModel: CoursesProgressModel
)