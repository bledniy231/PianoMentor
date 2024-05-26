package teachingsolutions.presentation_layer.fragments.piano

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.example.pianomentor.databinding.FragmentEnterPianoExerciseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterPianoExerciseFragment : Fragment() {

    private var _binding: FragmentEnterPianoExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: Bundle

    private val viewModel: EnterPianoLessonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPianoExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            args = requireArguments()
        } catch (e: Exception) {
            Toast.makeText(context, "Empty arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.enterPianoToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.exerciseName.text = args.getString("CourseItemTitle")

        view.postDelayed({
            if (binding.taskTextContainer.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.taskDescriptionCardView)
                TransitionManager.beginDelayedTransition(binding.buttonsContainer)
                binding.taskTextContainer.visibility = View.VISIBLE
            }
        }, 400)
    }
}