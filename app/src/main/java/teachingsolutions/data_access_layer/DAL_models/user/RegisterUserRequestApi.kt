package teachingsolutions.data_access_layer.DAL_models.user

data class RegisterUserRequestApi(
    val userName: String,
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val roles: List<String>
)