package teachingsolutions.data_access_layer.DAL_models.user

data class LoginUserRequestApi(
    val email: String,
    val password: String
)