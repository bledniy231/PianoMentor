package teachingsolutions.presentation_layer.fragments.piano

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentPianoBinding
import com.example.pianomentor.databinding.FragmentPianoLessonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PianoLessonFragment : Fragment() {

    private var _binding: FragmentPianoLessonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PianoLessonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPianoLessonBinding.inflate(inflater, container, false)
        return binding.root
    }
}