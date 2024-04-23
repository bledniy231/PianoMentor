package teachingsolutions.data_access_layer.DAL_models

data class LoginUserRequest(
    val email: String,
    val password: String
)