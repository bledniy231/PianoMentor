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

    private val _tempProfilePhoto: MutableLiveData<String> = MutableLiveData()
    val tempProfilePhoto: LiveData<String> = _tempProfilePhoto

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
                val path = getFilePathFromUri(context, uri, true)
                _tempProfilePhoto.postValue(path)
                val file = File(path)
                if (file == null) {
                    Toast.makeText(context, context.getString(R.string.error_loading_image), Toast.LENGTH_LONG).show()
                    return@withContext
                }

                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val result = userRepository.setProfilePhoto(body)
                _settingPhotoResult.postValue(result)
            }
        }
    }

    private fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String =
        if (uri.path?.contains("file://") == true) {
            uri.path!!
        } else {
            getFileFromContentUri(context, uri, uniqueName).path
        }

    private fun getFileFromContentUri(context: Context, contentUri: Uri, uniqueName: Boolean): File {
        val fileExtension = getFileExtension(context, contentUri) ?: ""
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = ("temp_file_" + if (uniqueName) timeStamp.plus(System.nanoTime()) else contentUri.path?.let(::File)?.name ?: "") + ".$fileExtension"
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()
        var oStream: FileOutputStream? = null
        var inputStream: InputStream? = null

        try {
            oStream = FileOutputStream(tempFile)
            inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let { copy(inputStream, oStream) }
            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            oStream?.close()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            uri.path?.let { MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString()) }
        }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }
}