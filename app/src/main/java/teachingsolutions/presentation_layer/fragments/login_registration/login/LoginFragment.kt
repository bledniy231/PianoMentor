package teachingsolutions.presentation_layer.fragments.login_registration.login

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //loginViewModel = ViewModelProvider(this, ViewModelsFactory())[LoginViewModel::class.java]

        val emailEditText = binding.loginEmailEditText
        val passwordEditText = binding.loginPasswordEditText
        val loginButton = binding.loginButton
        val loadingProgressBar = binding.loginLoading
        val loginToolbar = binding.loginToolbar

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                loginFormState ?: return@Observer

                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    emailEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
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
                    findNavController().navigate(R.id.action_successful_loggedIn)
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
            findNavController().navigate(R.id.action_back_arrow_login_to_choose)
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