package teachingsolutions.presentation_layer.fragments.change_password.model

data class ChangePasswordFormState(
    val passwordLengthError: String? = null,
    val passwordLowercaseError: String? = null,
    val passwordUppercaseError: String? = null,
    val passwordDigitError: String? = null,
    val confirmPasswordError: String? = null,
    val isDataValid: Boolean = false
)