package teachingsolutions.presentation_layer.fragments.login.model

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserModelUI? = null,
    val error: String? = null
)