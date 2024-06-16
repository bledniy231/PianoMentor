package teachingsolutions.presentation_layer.fragments.login_registration.login

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
import com.example.pianomentor.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.fragments.login_registration.common_model.LoggedInUserModelUI

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText = binding.loginEmailEditText
        val passwordEditText = binding.loginPasswordEditText
        val loginButton = binding.loginButton
        val loadingProgressBar = binding.loginLoading
        val loginToolbar = binding.loginToolbar
        val emailErrorTextView = binding.emailErrorLogin
        val passwordErrorTextView = binding.passwordErrorLogin

        passwordEditText.setOnTouchListener { _, event ->
            val drawableEnd = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[drawableEnd].bounds.width())) {
                    if (passwordEditText.transformationMethod is PasswordTransformationMethod) {
                        passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_eye_opened)
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                        Log.d("Login_PasswordEditText", "Password visibility visible")
                    } else {
                        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_eye_closed)
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                        Log.d("Login_PasswordEditText", "Password visibility gone")
                    }
                    passwordEditText.performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer { loginFormState ->
            loginFormState ?: return@Observer

            loginButton.isEnabled = loginFormState.isDataValid

            val passwordErrors = mutableListOf<String>()

            loginFormState.passwordLengthError?.let {
                passwordErrors.add(getString(it, "Пароль"))
            }
            loginFormState.passwordLowercaseError?.let {
                passwordErrors.add(getString(it, "Пароль"))
            }
            loginFormState.passwordUppercaseError?.let {
                passwordErrors.add(getString(it, "Пароль"))
            }
            loginFormState.passwordDigitError?.let {
                passwordErrors.add(getString(it, "Пароль"))
            }

            if (loginFormState.usernameError != null) {
                emailErrorTextView.visibility = View.VISIBLE
                emailErrorTextView.text = getString(loginFormState.usernameError)
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
        })

        loginViewModel.loginResultUI.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                    passwordEditText.text.clear()
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                    val options = NavOptions.Builder()
                        .setLaunchSingleTop(false)
                        .setPopUpTo(R.id.statisticsFragment, true)
                        .build()
                    findNavController().navigate(R.id.action_successful_loggedIn, null, options)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }

        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.visibility = View.VISIBLE
                loginViewModel.login(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        loginToolbar.setNavigationOnClickListener {
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