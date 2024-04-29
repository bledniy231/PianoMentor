package teachingsolutions.presentation_layer.fragments.login_registration.common_model

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResultUI(
    val success: LoggedInUserModelUI? = null,
    val error: String? = null
)