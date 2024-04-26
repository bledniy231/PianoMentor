package teachingsolutions.data_access_layer.DAL_models.user

data class LoginUserRequest(
    val email: String,
    val password: String
)