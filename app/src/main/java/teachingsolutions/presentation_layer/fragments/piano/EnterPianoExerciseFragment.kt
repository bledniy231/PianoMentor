package teachingsolutions.presentation_layer.fragments.piano

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentEnterPianoExerciseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class EnterPianoExerciseFragment : Fragment() {

    private var _binding: FragmentEnterPianoExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: Bundle

    private val viewModel: EnterPianoExerciseViewModel by viewModels()

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

        changeVisibility(View.GONE)
        viewModel.getExerciseTask(args.getInt("CourseItemId"))

        viewModel.exerciseTask.observe(viewLifecycleOwner) { exerciseTaskResponse ->
            exerciseTaskResponse ?: return@observe

            if (exerciseTaskResponse.error != null) {
                Toast.makeText(context, exerciseTaskResponse.error, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }

            binding.taskText.text = exerciseTaskResponse.success?.taskDescription
            binding.btnsLeft.removeAllViews()
            binding.btnsRight.removeAllViews()
            val buttons = exerciseTaskResponse.success?.intervalsInTask?.let { viewModel.getButtons(requireContext(), it) }

            for ((count, btn) in buttons?.withIndex()!!) {
                if (count % 2 == 0) {
                    binding.btnsLeft.addView(btn)
                } else {
                    binding.btnsRight.addView(btn)
                }
            }

            changeVisibility(View.VISIBLE)

            view.postDelayed({
                if (binding.taskTextContainer.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(binding.taskDescriptionCardView)
                    TransitionManager.beginDelayedTransition(binding.btnsContainer)
                    binding.taskTextContainer.visibility = View.VISIBLE
                }
            }, 400)
        }

        binding.enterPianoToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.exerciseName.text = args.getString("CourseItemTitle")

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_open_piano_exercise)
        }
    }

    private fun changeVisibility(visibilityCode: Int) {
        binding.taskContainer.visibility = visibilityCode
        binding.btnsContainer.visibility = visibilityCode
        binding.btnStart.visibility = visibilityCode
        binding.enterExerciseLoading.visibility = abs(visibilityCode - 8)
    }
}