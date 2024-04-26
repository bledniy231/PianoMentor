package teachingsolutions.data_access_layer.DAL_models.common

open class DefaultResponse(errors: Array<String>?) {
    val _errors: Array<String>? = errors
}