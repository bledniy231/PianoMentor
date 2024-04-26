package teachingsolutions.data_access_layer.DAL_models.user

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponse

class RefreshUserTokensResponse (
    val jwtTokens: JwtTokens? = null,
    val errors: Array<String>? = null
) : DefaultResponse(errors)