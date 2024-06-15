package teachingsolutions.presentation_layer.fragments.profile

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.common.FileResultUI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {

    private val _isLoggedOut: MutableLiveData<Boolean?> = MutableLiveData()
    val isLoggedOut: LiveData<Boolean?> = _isLoggedOut

    private val _profilePhoto: MutableLiveData<FileResultUI> = MutableLiveData()
    val profilePhoto: LiveData<FileResultUI> = _profilePhoto

    private val _settingPhotoResult: MutableLiveData<DefaultResponseUI> = MutableLiveData()
    val settingPhotoResult: LiveData<DefaultResponseUI> = _settingPhotoResult

    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = userRepository.logout(false)
                _isLoggedOut.postValue(result)
            }
        }
    }

    fun getProfilePhoto() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = userRepository.getProfilePhoto()
                _profilePhoto.postValue(result)
            }
        }
    }

    fun setProfilePhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = userRepository.setProfilePhoto(context, uri)
                _settingPhotoResult.postValue(result)
            }
        }
    }
}