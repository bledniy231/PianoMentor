package teachingsolutions.presentation_layer.fragments.login

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
import teachingsolutions.domain_layer.login_registration.UserRepository
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.presentation_layer.fragments.login.model.LoggedInUserModelUI
import teachingsolutions.presentation_layer.fragments.login.model.LoginFormState
import teachingsolutions.presentation_layer.fragments.login.model.LoginResult
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRegisterRepository: UserRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = loginRegisterRepository.login(username, password)) {
                    is ActionResult.Success -> {
                        _loginResult.postValue(LoginResult(success = LoggedInUserModelUI(displayName = result.data.userName)))
                    }
                    is ActionResult.ExceptionError -> {
                        _loginResult.postValue(LoginResult(error = result.exception.message))
                    }
                    is ActionResult.NormalError -> {
                        _loginResult.postValue(LoginResult(error = result.data.failedMessage))
                    }
                }
            }
        }
    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailInvalid(email)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (password.isNotEmpty() && !isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isEmailInvalid(username: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}