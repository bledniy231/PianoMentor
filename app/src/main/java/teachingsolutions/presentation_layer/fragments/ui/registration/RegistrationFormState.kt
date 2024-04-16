package teachingsolutions.presentation_layer.fragments.ui.registration

data class RegistrationFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isDataValid: Boolean = false
)