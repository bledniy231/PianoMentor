package teachingsolutions.presentation_layer.fragments.change_password

import android.content.Context
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
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.presentation_layer.fragments.change_password.model.ChangePasswordFormState
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _changePasswordForm = MutableLiveData<ChangePasswordFormState>()
    val changePasswordForm: LiveData<ChangePasswordFormState> = _changePasswordForm

    private val _changePasswordResult = MutableLiveData<DefaultResponseUI>()
    val changePasswordResult: LiveData<DefaultResponseUI> = _changePasswordResult

    fun changePassword(oldPass: String, newPass: String, repeatNewPass: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = userRepository.changePassword(oldPass, newPass, repeatNewPass)) {
                    is ActionResult.Success -> {
                        _changePasswordResult.postValue(DefaultResponseUI())
                    }
                    is ActionResult.NormalError -> {
                        _changePasswordResult.postValue(DefaultResponseUI(result.data.errors?.joinToString("\n")))
                    }
                    is ActionResult.ExceptionError -> {
                        _changePasswordResult.postValue(DefaultResponseUI(result.exception.message))
                    }
                }
            }
        }
    }

    fun passwordsDataChanged(context: Context, oldPass: String, newPass: String, repeatNewPass: String) {
        when {
            oldPass.length < 6 -> _changePasswordForm.value = ChangePasswordFormState(passwordLengthError = context.getString(R.string.password_too_short, "Старый пароль"))
            !containsLowercase(oldPass) -> _changePasswordForm.value = ChangePasswordFormState(passwordLowercaseError = context.getString(R.string.password_no_lowercase, "Старый пароль"))
            !containsUppercase(oldPass) -> _changePasswordForm.value = ChangePasswordFormState(passwordUppercaseError = context.getString(R.string.password_no_uppercase, "Старый пароль"))
            !containsDigit(oldPass) ->_changePasswordForm.value = ChangePasswordFormState(passwordDigitError = context.getString(R.string.password_no_digit, "Старый пароль"))
            newPass.length < 6 -> _changePasswordForm.value = ChangePasswordFormState(passwordLengthError = context.getString(R.string.password_too_short, "Новый пароль"))
            !containsLowercase(newPass) -> _changePasswordForm.value = ChangePasswordFormState(passwordLowercaseError = context.getString(R.string.password_no_lowercase, "Новый пароль"))
            !containsUppercase(newPass) -> _changePasswordForm.value = ChangePasswordFormState(passwordUppercaseError = context.getString(R.string.password_no_uppercase, "Новый пароль"))
            !containsDigit(newPass) ->_changePasswordForm.value = ChangePasswordFormState(passwordDigitError = context.getString(R.string.password_no_digit, "Новый пароль"))
            newPass != repeatNewPass -> _changePasswordForm.value = ChangePasswordFormState(confirmPasswordError =  context.getString(R.string.prompt_password_not_match))
            else -> _changePasswordForm.value = ChangePasswordFormState(isDataValid = true)
        }
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