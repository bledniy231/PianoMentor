package teachingsolutions.presentation_layer.fragments.login_registration.registration

import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.fragments.login_registration.common_model.LoggedInUserModelUI

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val registerViewModel: RegistrationViewModel by viewModels()
    private var _binding: FragmentRegistrationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //registerViewModel = ViewModelProvider(this, ViewModelsFactory())[RegistrationViewModel::class.java]

        val nicknameEditText = binding.registerNicknameEditText
        val emailEditText = binding.registerEmailEditText
        val passwordEditText = binding.registerPasswordEditText
        val confirmPasswordEditText = binding.registerConfirmPasswordEditText
        val registerButton = binding.registerButton
        val loadingProgressBar = binding.registerLoading

        registerViewModel.registerFormState.observe(viewLifecycleOwner,
            Observer { registerFormState ->
                if (registerFormState == null) {
                    return@Observer
                }
                registerButton.isEnabled = registerFormState.isDataValid
                registerFormState.usernameError?.let {
                    nicknameEditText.error = getString(it)
                }
                registerFormState.emailError?.let {
                    emailEditText.error = getString(it)
                }
                registerFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
                registerFormState.confirmPasswordError?.let {
                    confirmPasswordEditText.error = getString(it)
                }
            })

        registerViewModel.registerResult.observe(viewLifecycleOwner,
            Observer { registerResult ->
                registerResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                registerResult.error?.let {
                    showLoginFailed(it)
                    passwordEditText.text.clear()
                    confirmPasswordEditText.text.clear()
                }
                registerResult.success?.let {
                    updateUiWithUser(it)
//                    val fragmentManager = requireActivity().supportFragmentManager
//                    while (fragmentManager.backStackEntryCount > 0) {
//                        fragmentManager.popBackStackImmediate()
//                    }
//                    findNavController().navigate(R.id.action_successful_registered)
                    //findNavController().popBackStack(R.id.statisticsFragment, true)
                    val options = NavOptions.Builder()
                        .setLaunchSingleTop(false)
                        .setPopUpTo(R.id.statisticsFragment, true)
                        .build()
                    findNavController().navigate(R.id.action_successful_registered, null, options)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registerViewModel.registerDataChanged(
                    nicknameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    confirmPasswordEditText.text.toString()
                )
            }
        }
        nicknameEditText.addTextChangedListener(afterTextChangedListener)
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.register(
                    nicknameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    confirmPasswordEditText.text.toString()
                )
            }
            false
        }

        registerButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            registerViewModel.register(
                nicknameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
                confirmPasswordEditText.text.toString()
            )
        }

        binding.registerToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserModelUI) {
        val welcome = getString(R.string.welcome) + " " + model.displayName
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}