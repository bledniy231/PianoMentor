package teachingsolutions.presentation_layer.fragments.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {

    private val _isLoggedOut: MutableLiveData<Boolean?> = MutableLiveData()
    val isLoggedOut: MutableLiveData<Boolean?> = _isLoggedOut

    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = userRepository.logout(false)
                _isLoggedOut.postValue(result)
            }
        }
    }
}