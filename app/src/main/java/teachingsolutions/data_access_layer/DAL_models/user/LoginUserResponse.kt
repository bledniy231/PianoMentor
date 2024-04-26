package teachingsolutions.data_access_layer.DAL_models.user

data class LoginUserResponse(
    val userId: Long,
    val userName: String,
    val email: String,
    val jwtTokensModel: JwtTokens,
    val roles: List<String>,
    val failedMessage: String? = null
)