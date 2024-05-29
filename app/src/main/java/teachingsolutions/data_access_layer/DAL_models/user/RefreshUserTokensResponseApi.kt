package teachingsolutions.data_access_layer.DAL_models.user

import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi

class RefreshUserTokensResponseApi (
    val jwtTokens: JwtTokens? = null,
    errors: Array<String>? = null
) : DefaultResponseApi(errors)