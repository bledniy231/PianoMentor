package teachingsolutions.presentation_layer.fragments.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.data_access_layer.login_registration.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.logout()
            }
        }
    }
}