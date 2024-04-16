package teachingsolutions.presentation_layer.fragments.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.data.login_registration.LoginRegistrationRepository
import teachingsolutions.presentation_layer.fragments.data.common.ActionResult
import teachingsolutions.presentation_layer.fragments.ui.common.LoggedInUserModelUI
import teachingsolutions.presentation_layer.fragments.ui.common.LoginResult

class LoginViewModel(private val loginRegistrationRepository: LoginRegistrationRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRegistrationRepository.login(username, password)

        if (result is ActionResult.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserModelUI(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}