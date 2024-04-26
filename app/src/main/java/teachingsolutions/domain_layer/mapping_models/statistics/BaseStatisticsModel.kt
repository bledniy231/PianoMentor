package teachingsolutions.domain_layer.mapping_models.statistics

open class BaseStatisticsModel (
    _progressValueAbsolute: Int,
    _progressValueInPercent: Int,
    _text: String) {
    var progressValueAbsolute: Int = _progressValueAbsolute
    var progressValueInPercent: Int = _progressValueInPercent
    var text: String = _text
}