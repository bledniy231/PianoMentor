package teachingsolutions.data_access_layer.DAL_models.user

import java.time.LocalDateTime

data class JwtTokens(
    var accessToken: String,
    var accessTokenExpireTime: LocalDateTime? = null,
    var refreshToken: String,
    var refreshTokenExpireTime: LocalDateTime? = null
)