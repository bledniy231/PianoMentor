package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    private var iterationCounter: Int = 1
    private lateinit var controlButtons: ControlButtonsUI
    private lateinit var answerButtonsLines: MutableList<LinearLayout>
    private var isFirstIteration: Boolean = true

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

        binding.exerciseToolbar.title = args.getString("CourseItemTitle")
        viewModel.startFirstIteration(requireContext(), args.getInt("CourseItemId"))

        viewModel.exerciseTaskTextUI.observe(viewLifecycleOwner) { exerciseTaskTextUI ->
            exerciseTaskTextUI ?: return@observe
            binding.exerciseDescription.text = exerciseTaskTextUI.text
            binding.exerciseDescription.textSize = exerciseTaskTextUI.textSize
        }

        viewModel.didIntervalsPlayed.observe(viewLifecycleOwner) { didIntervalsPlayed ->
            didIntervalsPlayed ?: return@observe
            changeButtonsClickable(didIntervalsPlayed.intervalsPlayed)
        }

        binding.btnsExerciseContainer.removeAllViews()
        viewModel.exerciseAnswersUI.observe(viewLifecycleOwner) { exerciseAnswersUI ->
            exerciseAnswersUI ?: return@observe

            if (exerciseAnswersUI.answersPairs == null) {
                Toast.makeText(context, "Empty buttons", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }

            makeAllButtons(requireContext(), binding.exerciseDescription, exerciseAnswersUI.answersPairs)

            view.postDelayed({
                viewModel.playIntervals()
            }, 600)
        }
    }



    private fun makeAllButtons(context: Context,
                               title: TextView,
                               answers: List<Pair<String, Boolean>>) {
        if (isFirstIteration) {
            answerButtonsLines = mutableListOf()
        } else {
            for (line in answerButtonsLines) {
                for (btnIndex in 0 until line.childCount) {
                    val button = line.getChildAt(btnIndex) as MaterialButton
                    button.setBackgroundColor(context.getColor(R.color.first_purple))
                    button.text = answers[(button.tag as Int)].first
                    button.setOnClickListener {
                        onClickListenerChooseBtns(context, title, button, answers[(button.tag as Int)].second)
                    }

                    controlButtons.nextButton.setBackgroundColor(context.getColor(R.color.transparent))
                    controlButtons.exerciseCounter.text = context.getString(R.string.exercise_counter, iterationCounter)
                }
            }
            return
        }

        for ((count, answer) in answers.withIndex()) {
            if (count % 2 == 0) {
                val layout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                answerButtonsLines.add(layout)
            }

            val button = MaterialButton(context).apply {
                text = answer.first
                tag = count
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    PixelLengthManager.getPixelsFromDp(context, 40)
                )
                val margin = PixelLengthManager.getPixelsFromDp(context, 5)
                (params as ViewGroup.MarginLayoutParams).setMargins(margin, 0, margin, 0)
                textSize = 18f
                setOnClickListener {
                    onClickListenerChooseBtns(context, title, this, answer.second)
                }
                layoutParams = params
            }

            answerButtonsLines.last().addView(button)
        }

        for (line in answerButtonsLines) {
            binding.btnsExerciseContainer.addView(line)
        }

        makeControlButtons(requireContext(), binding.exerciseDescription)
        binding.btnsExerciseContainer.addView(controlButtons.frameLayout)
    }

    private fun onClickListenerChooseBtns(context: Context,
                                          title: TextView,
                                          currentButton: MaterialButton,
                                          isCorrect: Boolean) {
        val strId = if (isCorrect) R.string.correct else R.string.incorrect
        val colorId = if (isCorrect) R.color.first_green else R.color.first_red

        title.text = context.getString(strId)
        title.textSize = PixelLengthManager.getPixelsFromSp(context, 24f)
        title.setTextColor(context.getColor(colorId))

        for (line in answerButtonsLines) {
            for (btnIndex in 0 until line.childCount) {
                val btn = line.getChildAt(btnIndex) as MaterialButton
                btn.isClickable = false
                btn.setBackgroundColor(context.getColor(R.color.light_gray))

                controlButtons.nextButton.setBackgroundColor(context.getColor(R.color.bright_blue))
                controlButtons.nextButton.text = context.getString(R.string.go_next_interval)
                controlButtons.repeatButton.isClickable = false
            }
        }

        currentButton.setBackgroundColor(context.getColor(colorId))
    }

    private fun makeControlButtons(context: Context, title: TextView) {
        val frameLayout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.actionBarSize)
            ).apply {
                gravity = Gravity.BOTTOM
                setPadding(
                    PixelLengthManager.getPixelsFromDp(context, 16), 0,
                    PixelLengthManager.getPixelsFromDp(context, 16), 0
                )
            }
        }

        val repeatButton = MaterialButton(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                PixelLengthManager.getPixelsFromDp(context, 40)
            ).apply {
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
            setTextColor(ContextCompat.getColor(context, R.color.first_purple))
            setPadding(
                PixelLengthManager.getPixelsFromDp(context, 5),
                0,
                PixelLengthManager.getPixelsFromDp(context, 5),
                0
            )
            setOnClickListener {
                viewModel.playIntervals()
            }
            isClickable = false
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            text = context.getString(R.string.repeat_btn)
        }

        val exerciseCounter = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            textSize = 18f
            text = context.getString(R.string.exercise_counter, iterationCounter)
        }

        val nextButton = MaterialButton(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                PixelLengthManager.getPixelsFromDp(context, 40)
            ).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            }
            setTextColor(ContextCompat.getColor(context, R.color.first_purple))
            setPadding(
                PixelLengthManager.getPixelsFromDp(context, 5),
                0,
                PixelLengthManager.getPixelsFromDp(context, 5),
                0
            )
            setOnClickListener {
                if (!controlButtons.repeatButton.isClickable) {
                    iterationCounter++
                }
                viewModel.nextIteration(context, title)
            }
            isClickable = false
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            text = context.getString(R.string.skip_btn)
        }

        frameLayout.addView(repeatButton)
        frameLayout.addView(exerciseCounter)
        frameLayout.addView(nextButton)
        controlButtons = ControlButtonsUI(frameLayout, repeatButton, exerciseCounter, nextButton)
    }

    private fun changeButtonsClickable(isClickable: Boolean) {
        controlButtons.nextButton.isClickable = isClickable
        controlButtons.repeatButton.isClickable = isClickable
        for (line in answerButtonsLines) {
            for (btnIndex in 0 until line.childCount) {
                val btn = line.getChildAt(btnIndex) as MaterialButton
                btn.isClickable = isClickable
            }
        }
    }
}