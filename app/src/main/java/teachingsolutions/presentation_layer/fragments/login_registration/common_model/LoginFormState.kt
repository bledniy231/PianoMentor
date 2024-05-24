package teachingsolutions.presentation_layer.fragments.login_registration.common_model

data class LoginFormState(
    val usernameError: Int? = null,
    val passwordLengthError: Int? = null,
    val passwordLowercaseError: Int? = null,
    val passwordUppercaseError: Int? = null,
    val passwordDigitError: Int? = null,
    val isDataValid: Boolean = false
)
