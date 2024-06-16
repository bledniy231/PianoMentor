package teachingsolutions.presentation_layer.fragments.login_registration.registration

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nicknameEditText = binding.registerNicknameEditText
        val nicknameErrorTextView = binding.nickNameErrorRegister
        val emailEditText = binding.registerEmailEditText
        val emailErrorTextView = binding.emailErrorRegister
        val passwordEditText = binding.registerPasswordEditText
        val passwordErrorTextView = binding.passwordErrorRegister
        val confirmPasswordEditText = binding.registerConfirmPasswordEditText
        val confirmPasswordTextView = binding.confirmPasswordErrorRegister
        val registerButton = binding.registerButton
        val loadingProgressBar = binding.registerLoading

        passwordEditText.setOnTouchListener { _, event ->
            val drawableEnd = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[drawableEnd].bounds.width())) {
                    if (passwordEditText.transformationMethod is PasswordTransformationMethod) {
                        passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_eye_opened)
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                        Log.d("Register_PasswordEditText", "Password visibility visible")
                    } else {
                        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        confirmPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_eye_closed)
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                        Log.d("Register_PasswordEditText", "Password visibility gone")
                    }
                    passwordEditText.performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }

        registerViewModel.registerFormState.observe(viewLifecycleOwner,
            Observer { registerFormState ->
                registerFormState ?: return@Observer

                registerButton.isEnabled = registerFormState.isDataValid

                val passwordErrors = mutableListOf<String>()

                registerFormState.passwordLengthError?.let {
                    passwordErrors.add(getString(it, "Пароль"))
                }
                registerFormState.passwordLowercaseError?.let {
                    passwordErrors.add(getString(it, "Пароль"))
                }
                registerFormState.passwordUppercaseError?.let {
                    passwordErrors.add(getString(it, "Пароль"))
                }
                registerFormState.passwordDigitError?.let {
                    passwordErrors.add(getString(it, "Пароль"))
                }

                if (registerFormState.usernameError != null) {
                    nicknameErrorTextView.visibility = View.VISIBLE
                    nicknameErrorTextView.text = getString(registerFormState.usernameError)
                } else {
                    nicknameErrorTextView.visibility = View.GONE
                    nicknameErrorTextView.text = null
                }

                if (registerFormState.emailError != null) {
                    emailErrorTextView.visibility = View.VISIBLE
                    emailErrorTextView.text = getString(registerFormState.emailError)
                } else {
                    emailErrorTextView.visibility = View.GONE
                    emailErrorTextView.text = null
                }

                if (passwordErrors.isNotEmpty()) {
                    passwordErrorTextView.visibility = View.VISIBLE
                    passwordErrorTextView.text = passwordErrors.joinToString(" ")
                } else {
                    passwordErrorTextView.visibility = View.GONE
                    passwordErrorTextView.text = null
                }

                if (registerFormState.confirmPasswordError != null) {
                    confirmPasswordTextView.visibility = View.VISIBLE
                    confirmPasswordTextView.text = getString(registerFormState.confirmPasswordError)
                } else {
                    confirmPasswordTextView.visibility = View.GONE
                    confirmPasswordTextView.text = null
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
                    val options = NavOptions.Builder()
                        .setLaunchSingleTop(false)
                        .setPopUpTo(R.id.statisticsFragment, true)
                        .build()
                    findNavController().navigate(R.id.action_successful_registered, null, options)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
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