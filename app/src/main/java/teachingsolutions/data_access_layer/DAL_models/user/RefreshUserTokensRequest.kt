package teachingsolutions.data_access_layer.DAL_models.user

data class RefreshUserTokensRequest(
    val jwtTokens: JwtTokens
)