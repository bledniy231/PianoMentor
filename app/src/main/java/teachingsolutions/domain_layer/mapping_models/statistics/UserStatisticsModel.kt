package teachingsolutions.domain_layer.mapping_models.statistics

data class UserStatisticsModel(
    var statListViewPagerItems: List<StatisticsViewPagerItemModel>,
    var exercisesProgressModel: ExercisesProgressModel,
    var lecturesProgressModel: LecturesProgressModel,
    var coursesProgressModel: CoursesProgressModel
)