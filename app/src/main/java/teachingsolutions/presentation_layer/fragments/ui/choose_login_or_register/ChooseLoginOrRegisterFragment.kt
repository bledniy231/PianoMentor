package teachingsolutions.presentation_layer.fragments.ui.choose_login_or_register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentChooseLoginOrRegisterBinding


class ChooseLoginOrRegisterFragment : Fragment() {

    private var _binding: FragmentChooseLoginOrRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLoginOrRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.regOrLoginToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_back_arrow_choose_to_statisticsFragment)
        }
        binding.startLoginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_start_login)
        }
        binding.startRegisterBtn.setOnClickListener {
            findNavController().navigate(R.id.action_start_register)
        }
    }

    companion object {
        fun newInstance() = ChooseLoginOrRegisterFragment()
    }
}