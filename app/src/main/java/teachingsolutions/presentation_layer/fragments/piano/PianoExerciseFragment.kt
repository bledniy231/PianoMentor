package teachingsolutions.presentation_layer.fragments.piano

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentPianoExerciseBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.common.PixelLengthManager
import teachingsolutions.presentation_layer.fragments.piano.model.ControlButtonsUI

@AndroidEntryPoint
class PianoExerciseFragment : Fragment() {

    private var _binding: FragmentPianoExerciseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PianoExerciseViewModel by viewModels()
    private lateinit var args: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPianoExerciseBinding.inflate(inflater, container, false)
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

        viewModel.startFirstIteration(requireContext(), args.getInt("CourseItemId"), binding.exerciseDescription)
        binding.btnsExerciseContainer.removeAllViews()
        viewModel.getControlButtons(requireContext(), binding.exerciseDescription)
        viewModel.exerciseModelUI.observe(viewLifecycleOwner) { exerciseModelUI ->
            exerciseModelUI ?: return@observe

            if (exerciseModelUI.buttonsLine == null) {
                Toast.makeText(context, "Empty buttons", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

            for (buttonLine in exerciseModelUI.buttonsLine!!) {
                binding.btnsExerciseContainer.addView(buttonLine)
            }

            viewModel.controlButtons.observe(viewLifecycleOwner) controlButtons@ { controlButtonsUI ->
                controlButtonsUI ?: return@controlButtons
                binding.btnsExerciseContainer.addView(controlButtonsUI.frameLayout)
            }

            view.postDelayed({
                viewModel.playIntervals()
            }, 600)
        }
    }
}