package teachingsolutions.presentation_layer.fragments.registration

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.domain_layer.login_registration.UserRepository
import teachingsolutions.presentation_layer.fragments.login.model.LoggedInUserModelUI
import teachingsolutions.presentation_layer.fragments.login.model.LoginResult
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegistrationFormState>()
    val registerFormState: LiveData<RegistrationFormState> = _registerForm

    private val _registerResult = MutableLiveData<LoginResult>()
    val registerResult: LiveData<LoginResult> = _registerResult

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = userRepository.register(username, email, password, confirmPassword)) {
                    is ActionResult.Success -> {
                        _registerResult.postValue(LoginResult(success = LoggedInUserModelUI(displayName = result.data.userName)))
                    }
                    is ActionResult.ExceptionError -> {
                        _registerResult.postValue(LoginResult(error = result.exception.message))
                    }
                    is ActionResult.NormalError -> {
                        _registerResult.postValue(LoginResult(error = result.data.failedMessage))
                    }
                }
            }
        }
    }

    fun registerDataChanged(username: String, email: String, password: String, confirmPassword: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegistrationFormState(usernameError = R.string.prompt_nickname_only_letters_numbers)
        } else if (email.isNotEmpty() && !isEmailValid(email)) {
            _registerForm.value = RegistrationFormState(emailError = R.string.prompt_email_not_regex)
        } else if (password.isNotEmpty() && !isPasswordShort(password)) {
            _registerForm.value = RegistrationFormState(passwordError = R.string.prompt_password_short)
        } else if (password.isNotEmpty() && confirmPassword.isNotEmpty() && !isPasswordMatch(password, confirmPassword)) {
            _registerForm.value = RegistrationFormState(confirmPasswordError = R.string.prompt_password_not_match)
        } else {
            _registerForm.value = RegistrationFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9]*$"))
    }

    private fun isEmailValid(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordShort(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}