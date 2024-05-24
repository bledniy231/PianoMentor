package teachingsolutions.presentation_layer.fragments.login_registration.common_model

data class RegistrationFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordLengthError: Int? = null,
    val passwordLowercaseError: Int? = null,
    val passwordUppercaseError: Int? = null,
    val passwordDigitError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isDataValid: Boolean = false
)