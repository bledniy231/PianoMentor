package teachingsolutions.presentation_layer.fragments.login_registration.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.core.util.PatternsCompat
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.presentation_layer.fragments.login_registration.common_model.LoggedInUserModelUI
import teachingsolutions.presentation_layer.fragments.login_registration.common_model.LoginFormState
import teachingsolutions.presentation_layer.fragments.login_registration.common_model.LoginResultUI
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRegisterRepository: UserRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResultUI = MutableLiveData<LoginResultUI>()
    val loginResultUI: LiveData<LoginResultUI> = _loginResultUI

    fun login(username: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = loginRegisterRepository.login(username, password)) {
                    is ActionResult.Success -> {
                        _loginResultUI.postValue(LoginResultUI(success = LoggedInUserModelUI(displayName = result.data.userName)))
                    }
                    is ActionResult.ExceptionError -> {
                        _loginResultUI.postValue(LoginResultUI(error = result.exception.message))
                    }
                    is ActionResult.NormalError -> {
                        _loginResultUI.postValue(LoginResultUI(error = result.data.failedMessage))
                    }
                }
            }
        }
    }

    fun loginDataChanged(email: String, password: String) {
        when {
            !isEmailValid(email) -> {
                _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
            }
            password.length < 6 -> {
                _loginForm.value = LoginFormState(passwordLengthError = R.string.password_too_short)
            }
            !containsLowercase(password) -> {
                _loginForm.value = LoginFormState(passwordLowercaseError = R.string.password_no_lowercase)
            }
            !containsUppercase(password) -> {
                _loginForm.value = LoginFormState(passwordUppercaseError = R.string.password_no_uppercase)
            }
            !containsDigit(password) -> {
                _loginForm.value = LoginFormState(passwordDigitError = R.string.password_no_digit)
            }
            else -> {
                _loginForm.value = LoginFormState(isDataValid = true)
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun containsLowercase(password: String): Boolean {
        return password.isNotEmpty() && password.any { it.isLowerCase() }
    }

    private fun containsUppercase(password: String): Boolean {
        return password.isNotEmpty() && password.any { it.isUpperCase() }
    }

    private fun containsDigit(password: String): Boolean {
        return password.isNotEmpty() && password.any { it.isDigit() }
    }
}