package teachingsolutions.domain_layer.mapping_models.statistics

class CoursesProgressModel(
    var _progressValueAbsolute: Int,
    var _progressValueInPercent: Int,
    var _text: String)
    : BaseStatisticsModel(_progressValueAbsolute, _progressValueInPercent, _text) { }