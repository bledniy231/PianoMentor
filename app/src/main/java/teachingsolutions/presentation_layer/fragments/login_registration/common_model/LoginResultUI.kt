package teachingsolutions.presentation_layer.fragments.login_registration.common_model

data class LoginResultUI(
    val success: LoggedInUserModelUI? = null,
    val error: String? = null
)