package teachingsolutions.presentation_layer.fragments.common

import teachingsolutions.presentation_layer.fragments.common.LoggedInUserModelUI

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserModelUI? = null,
    val error: Int? = null
)