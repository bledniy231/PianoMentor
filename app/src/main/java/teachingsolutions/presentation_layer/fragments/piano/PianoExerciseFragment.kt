package teachingsolutions.presentation_layer.fragments.piano

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pianomentor.databinding.FragmentPianoExerciseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PianoExerciseFragment : Fragment() {

    private var _binding: FragmentPianoExerciseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PianoExerciseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPianoExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }
}