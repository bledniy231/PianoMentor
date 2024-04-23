package teachingsolutions.data_access_layer.DAL_models

data class LoginUserResponse(
    val userId: Long,
    val userName: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val roles: List<String>,
    val failedMessage: String? = null
)