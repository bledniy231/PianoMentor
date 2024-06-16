package teachingsolutions.presentation_layer.fragments.change_password

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oldPassword = binding.oldPasswordEditText
        val newPassword = binding.newPasswordEditText
        val repeatNewPassword = binding.repeatNewPasswordEditText
        val button = binding.changePasswordButton
        val errorField = binding.changePasswordError

        oldPassword.setOnTouchListener { _, event ->
            val drawableEnd = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (oldPassword.right - oldPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    if (oldPassword.transformationMethod is PasswordTransformationMethod) {
                        oldPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        newPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        repeatNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_eye_opened)
                        oldPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                        Log.d("ChangePassword_PasswordEditText", "Password visibility visible")
                    } else {
                        oldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        newPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        repeatNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_eye_closed)
                        oldPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                        Log.d("ChangePassword_PasswordEditText", "Password visibility gone")
                    }
                    oldPassword.performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }

        changePasswordViewModel.changePasswordForm.observe(viewLifecycleOwner) { changePasswordFormState ->
            changePasswordFormState ?: return@observe

            button.isEnabled = changePasswordFormState.isDataValid

            when {
                changePasswordFormState.passwordLengthError != null -> {
                    errorField.visibility = View.VISIBLE
                    errorField.text = changePasswordFormState.passwordLengthError
                }
                changePasswordFormState.passwordLowercaseError != null -> {
                    errorField.visibility = View.VISIBLE
                    errorField.text = changePasswordFormState.passwordLowercaseError
                }
                changePasswordFormState.passwordUppercaseError != null -> {
                    errorField.visibility = View.VISIBLE
                    errorField.text = changePasswordFormState.passwordUppercaseError
                }
                changePasswordFormState.passwordDigitError != null -> {
                    errorField.visibility = View.VISIBLE
                    errorField.text = changePasswordFormState.passwordDigitError
                }
                changePasswordFormState.confirmPasswordError != null -> {
                    errorField.visibility = View.VISIBLE
                    errorField.text = changePasswordFormState.confirmPasswordError
                }
                else -> {
                    errorField.visibility = View.GONE
                    repeatNewPassword.error = null
                }
            }
        }

        changePasswordViewModel.changePasswordResult.observe(viewLifecycleOwner) { changePasswordResult ->
            changePasswordResult ?: return@observe

            binding.changePasswordLoading.visibility = View.GONE
            if (changePasswordResult.message == null) {
                updateUiSuccess()
                findNavController().popBackStack()
            } else {
                showChangePasswordFailed(changePasswordResult.message)
            }
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable) {
                changePasswordViewModel.passwordsDataChanged(
                    requireContext(),
                    oldPassword.text.toString(),
                    newPassword.text.toString(),
                    repeatNewPassword.text.toString()
                )
            }
        }

        oldPassword.addTextChangedListener(afterTextChangedListener)
        newPassword.addTextChangedListener(afterTextChangedListener)
        repeatNewPassword.addTextChangedListener(afterTextChangedListener)

        button.setOnClickListener {
            binding.changePasswordLoading.visibility = View.VISIBLE
            changePasswordViewModel.changePassword(
                oldPassword.text.toString(),
                newPassword.text.toString(),
                repeatNewPassword.text.toString()
            )
        }

        binding.changePasswordToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateUiSuccess() {
        val welcome = getString(R.string.changed_password_success)
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show()
    }

    private fun showChangePasswordFailed(errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}