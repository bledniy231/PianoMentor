package teachingsolutions.presentation_layer.fragments.data.statistics.model

class LecturesProgressModel(
    var _progressValueAbsolute: Int,
    var _progressValueInPercent: Int,
    var _text: String)
    : BaseStatisticsModel(_progressValueAbsolute, _progressValueInPercent, _text) { }