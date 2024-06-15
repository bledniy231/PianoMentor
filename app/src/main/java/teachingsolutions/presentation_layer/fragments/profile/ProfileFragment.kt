package teachingsolutions.presentation_layer.fragments.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.customViews.AvatarView.internal.dp
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.math.abs

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

//    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//        if (uri == null) {
//            Toast.makeText(requireContext(), getString(R.string.error_loading_image), Toast.LENGTH_LONG).show()
//            return@registerForActivityResult
//        }
//
//        binding.userAvatar.setAvatarImageOrInitials(uri.toString(), "ES")
//        viewModel.setProfilePhoto(requireContext(), uri)
//    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = result.data?.data?.path?.let { File(it) }
            if (file == null) {
                Toast.makeText(requireContext(), getString(R.string.error_loading_image), Toast.LENGTH_LONG).show()
                return@registerForActivityResult
            }

            viewModel.tempProfilePhoto.observe(viewLifecycleOwner) { path ->
                path ?: return@observe

                binding.userAvatar.setAvatarImageOrInitials(result.data?.data?.toString(), "ES")
            }

            viewModel.setProfilePhoto(requireContext(), result.data?.data!!)
        }
    }


//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            if (isGranted) {
//                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//                val request = PickVisualMediaRequest.Builder()
//                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                    .build()
//                launcher.launch(intent)
//            } else {
//                Toast.makeText(requireContext(), "Нет прав доступа к фото", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun requestPermission() {
//        when {
//            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
//                AlertDialog.Builder(requireContext())
//                    .setTitle("Необходимо разрешение")
//                    .setMessage("Разрешите приложению PianoMentor использовать фотографии с Вашего устройства")
//                    .setPositiveButton("Ok") { _, _ ->
//                        requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
//                    }
//                    .setNegativeButton("Отмена", null)
//                    .create()
//                    .show()
//            }
//            else -> {
//                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
//            }
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel.getProfilePhoto()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleTopAppBar()

        viewModel.profilePhoto.observe(viewLifecycleOwner) { fileResultUI ->
            fileResultUI ?: return@observe

            if (fileResultUI.success != null) {
                binding.userAvatar.visibility = View.VISIBLE
                binding.userAvatar.setAvatarImageOrInitials(fileResultUI.success.path, "ES")
            }
            else if (fileResultUI.error != null) {
                Toast.makeText(requireContext(), fileResultUI.error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.settingPhotoResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe

            if (result.message != null) {
                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.usernameProfile.text = viewModel.userRepository.userName

        binding.profileToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.aboutAppCard.setOnClickListener {
            findNavController().navigate(R.id.action_open_about_app)
        }

        binding.logoutCard.setOnClickListener {
            if (!viewModel.userRepository.isLoggedIn) {
                Toast.makeText(requireContext(),
                    getString(R.string.you_havent_been_already_logged_in), Toast.LENGTH_SHORT).show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.logout_title_dialog))
                    .setMessage(getString(R.string.are_you_sure_logout_dialog))
                    .setPositiveButton(getString(R.string.yes_word)) { _, _ ->
                        viewModel.logout()
                        viewModel.isLoggedOut.observe(viewLifecycleOwner) { isLoggedOut ->
                            isLoggedOut ?: return@observe

                            updateUiWithLogoutResult(isLoggedOut)
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton(getString(R.string.no_word), null)
                    .show()
            }
        }

        binding.uploadProfilePhotoCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            launcher.launch(intent)
            //launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun handleTopAppBar() {
        val initialSize = 175.dp
        val finalSize = 64.dp

        val initialTranslationY = 66.dp
        val finalTranslationY = 25.dp

        var currentSize = initialSize
        var currentTranslationY = initialTranslationY

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset.toFloat()) / appBarLayout.totalScrollRange
            val newSize = (initialSize - (initialSize - finalSize) * percentage).toInt()
            val newTranslationY =
                (initialTranslationY - (initialTranslationY - finalTranslationY) * percentage).toInt()

            if (newSize != currentSize || newTranslationY != currentTranslationY) {
                currentSize = newSize
                currentTranslationY = newTranslationY

                updateAvatarViewParams(newSize, newTranslationY)
            }
        }
    }

    private fun updateAvatarViewParams(newSize: Int, newTranslationY: Int) {
        val layoutParams = binding.userAvatar.layoutParams
        layoutParams.width = newSize
        layoutParams.height = newSize
        binding.userAvatar.layoutParams = layoutParams
        binding.userAvatar.translationY = newTranslationY.toFloat()

        binding.userAvatar.requestLayout()
    }

    private fun updateUiWithLogoutResult(isLoggedOut: Boolean) {
        if (isLoggedOut) {
            Toast.makeText(requireContext(), getString(R.string.you_have_been_logged_out), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}