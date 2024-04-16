package teachingsolutions.presentation_layer.fragments.ui.registration

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.data.common.ActionResult
import teachingsolutions.presentation_layer.fragments.data.login_registration.LoginRegistrationRepository
import teachingsolutions.presentation_layer.fragments.ui.common.LoggedInUserModelUI
import teachingsolutions.presentation_layer.fragments.ui.common.LoginResult

class RegistrationViewModel(private val loginRegisterRepository: LoginRegistrationRepository) : ViewModel() {
    private val _registerForm = MutableLiveData<RegistrationFormState>()
    val registerFormState: LiveData<RegistrationFormState> = _registerForm

    private val _registerResult = MutableLiveData<LoginResult>()
    val registerResult: LiveData<LoginResult> = _registerResult

    fun login(username: String, email: String, password: String, confirmPassword: String) {
        // can be launched in a separate asynchronous job
        val result = loginRegisterRepository.register(username, email, password, confirmPassword)

        if (result is ActionResult.Success) {
            _registerResult.value =
                LoginResult(success = LoggedInUserModelUI(displayName = result.data.displayName))
        } else {
            _registerResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun registerDataChanged(username: String, email: String, password: String, confirmPassword: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegistrationFormState(usernameError = R.string.prompt_nickname_only_letters_numbers)
        } else if (isEmailValid(email)) {
            _registerForm.value = RegistrationFormState(emailError = R.string.prompt_email_not_regex)
        }
        else if (!isPasswordShort(password)) {
            _registerForm.value = RegistrationFormState(passwordError = R.string.prompt_password_short)
        } else if (!isPasswordMatch(password, confirmPassword)) {
            _registerForm.value = RegistrationFormState(confirmPasswordError = R.string.prompt_password_not_match)
        } else {
            _registerForm.value = RegistrationFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9]*$"))
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordShort(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}