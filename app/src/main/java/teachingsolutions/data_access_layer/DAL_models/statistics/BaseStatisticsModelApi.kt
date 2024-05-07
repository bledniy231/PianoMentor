package teachingsolutions.data_access_layer.DAL_models.statistics

data class BaseStatisticsModelApi (
    var title: String,
    var progressValueAbsolute: Int,
    var progressValueInPercent: Int
)