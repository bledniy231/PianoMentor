package teachingsolutions.presentation_layer.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.customViews.AvatarView.internal.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTopAppBar()
        binding.profileToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.aboutAppCard.setOnClickListener {
            findNavController().navigate(R.id.action_open_about_app)
        }
        binding.usernameProfile.text = viewModel.userRepository.userName
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
                        Toast.makeText(requireContext(),
                            getString(R.string.you_have_been_logged_out), Toast.LENGTH_SHORT).show()
                        //findNavController().popBackStack(R.id.statisticsFragment, true)
//                        val options = NavOptions.Builder()
//                            .setLaunchSingleTop(false)
//                            .setPopUpTo(R.id.statisticsFragment, true)
//                            .build()
//                        findNavController().navigate(R.id.action_back_arrow_profile_to_statistics, null, options)
                        findNavController().popBackStack()
                    }
                    .setNegativeButton(getString(R.string.no_word), null)
                    .show()
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}