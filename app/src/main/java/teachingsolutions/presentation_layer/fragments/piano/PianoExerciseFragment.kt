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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentPianoExerciseBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.common.PixelLengthManager
import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
import teachingsolutions.presentation_layer.custom_views.piano_view.NoteIconView
import teachingsolutions.presentation_layer.custom_views.piano_view.PianoView
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.piano.model.AnswerModel
import teachingsolutions.presentation_layer.fragments.piano.model.ControlButtonsUI

@AndroidEntryPoint
class PianoExerciseFragment : Fragment() {

    private var _binding: FragmentPianoExerciseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PianoExerciseViewModel by viewModels()
    private lateinit var args: Bundle

    private var iterationCounter: Int = 1
    private var correctAnswersCounter: Int = 0
    private lateinit var controlButtons: ControlButtonsUI
    private lateinit var answerButtonsLines: MutableList<LinearLayout>
    private var isFirstIteration: Boolean = true
    private var answers: List<AnswerModel>? = null


    private val exerciseSavingResultBACKObserver = Observer<DefaultResponseUI?> { result ->
        findNavController().popBackStack()
        result ?: return@Observer

        if (result.message != null) {
            Toast.makeText(requireContext(), "FAIL: Error while saving exercise result", Toast.LENGTH_LONG).show()
        }
    }

    private val exerciseSavingResultCOMPLETEObserver = Observer<DefaultResponseUI?> { result ->
        result ?: return@Observer

        if (result.message != null) {
            Toast.makeText(requireContext(), "FAIL: Error while saving exercise result", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        val (correctDefaultAnswers, correctUserAnswers, correctAnswersPercentage) = viewModel.calculateExerciseResults(iterationCounter, correctAnswersCounter)
        val bundle = Bundle().apply {
            putInt("CourseId", args.getInt("CourseId"))
            putInt("CourseItemId", args.getInt("CourseItemId"))
            putString("CourseItemTitle", args.getString("CourseItemTitle"))
            putInt("CorrectUserAnswers", correctUserAnswers)
            putInt("CorrectDefaultAnswers", correctDefaultAnswers)
            putDouble("CorrectAnswersPercentage", correctAnswersPercentage)
            putBoolean("IsQuiz", false)
        }
        findNavController().navigate(R.id.action_open_quiz_result, bundle)
    }




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

        val courseId = args.getInt("CourseId")
        val courseItemId = args.getInt("CourseItemId")
        val courseItemTitle = args.getString("CourseItemTitle")

        binding.exerciseToolbar.title = courseItemTitle
        viewModel.startFirstIteration(requireContext(), courseItemId)

        binding.exerciseToolbar.setNavigationOnClickListener {
            if (iterationCounter == 10) {
                when (correctAnswersCounter) {
                    10 -> viewModel.setExerciseResult(courseId, courseItemId, CourseItemProgressType.COMPLETED)
                    else -> viewModel.setExerciseResult(courseId, courseItemId, CourseItemProgressType.FAILED)
                }
            } else {
                viewModel.setExerciseResult(courseId, courseItemId, CourseItemProgressType.IN_PROGRESS)
            }

            viewModel.progressSavingResult.observe(viewLifecycleOwner, exerciseSavingResultBACKObserver)
        }

        viewModel.exerciseTaskTextUI.observe(viewLifecycleOwner) { exerciseTaskTextUI ->
            exerciseTaskTextUI ?: return@observe
            binding.exerciseDescription.text = exerciseTaskTextUI.text
            binding.exerciseDescription.textSize = exerciseTaskTextUI.textSize
            binding.exerciseDescription.setTextColor(exerciseTaskTextUI.textColor)
        }

        viewModel.didIntervalsPlayed.observe(viewLifecycleOwner) { didIntervalsPlayed ->
            didIntervalsPlayed ?: return@observe
            changeButtonsEnabled(didIntervalsPlayed.intervalsPlayed)
        }

        binding.btnsExerciseContainer.removeAllViews()
        viewModel.exerciseAnswersUI.observe(viewLifecycleOwner) { exerciseAnswersUI ->
            exerciseAnswersUI ?: return@observe

            if (exerciseAnswersUI.answers == null) {
                Toast.makeText(context, "Empty buttons", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }

            makeAllButtons(requireContext(), binding.exerciseDescription, exerciseAnswersUI.answers)
            invisibleAllNoteIcons()
            showNoteIcon(exerciseAnswersUI.answers.first().firstNoteName, shouldScroll = true)
            answers = exerciseAnswersUI.answers

            view.postDelayed({
                viewModel.playIntervals()
            }, 600)
        }
    }



    private fun makeAllButtons(context: Context,
                               title: TextView,
                               answers: List<AnswerModel>) {
        if (isFirstIteration) {
            answerButtonsLines = mutableListOf()
            isFirstIteration = false
        } else {
            for (line in answerButtonsLines) {
                for (btnIndex in 0 until line.childCount) {
                    val button = line.getChildAt(btnIndex) as MaterialButton
                    button.setBackgroundColor(context.getColor(R.color.first_purple))
                    button.text = answers[(button.tag as Int)].answerName
                    button.setOnClickListener {
                        onClickListenerChooseBtns(context, title, button, answers[(button.tag as Int)].isCorrect)
                    }

                    controlButtons.nextButton.setBackgroundColor(context.getColor(R.color.transparent))
                    controlButtons.nextButton.text = context.getString(R.string.skip_btn)
                    controlButtons.exerciseCounter.text = context.getString(R.string.exercise_counter, iterationCounter)
                }
            }
            return
        }

        for ((count, answer) in answers.withIndex()) {
            if (count % 2 == 0) {
                val layout = LinearLayout(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }

                answerButtonsLines.add(layout)
            }

            val button = MaterialButton(context).apply {
                text = answer.answerName
                tag = count
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val margin = PixelLengthManager.getPixelsFromDp(context, 5)
                (params as ViewGroup.MarginLayoutParams).setMargins(margin, 0, margin, 0)
                textSize = 18f

                setOnClickListener {
                    onClickListenerChooseBtns(context, title, this, answer.isCorrect)
                }
                layoutParams = params
            }

            answerButtonsLines.last().addView(button)
        }

        for (line in answerButtonsLines) {
            binding.btnsExerciseContainer.addView(line)
        }

        makeControlButtons(requireContext())
        binding.btnsExerciseContainer.addView(controlButtons.frameLayout)
    }

    private fun onClickListenerChooseBtns(context: Context,
                                          title: TextView,
                                          currentButton: MaterialButton,
                                          isCorrect: Boolean) {
        changeButtonsEnabled(false)
        controlButtons.nextButton.isEnabled = true
        controlButtons.nextButton.setBackgroundColor(context.getColor(R.color.bright_blue))
        controlButtons.nextButton.text = if (iterationCounter == 10) context.getString(R.string.go_next_interval) else context.getString(R.string.end_exercise)

        val correctAnswer = answers?.find { it.isCorrect }
        val (strId, colorId) = when {
            isCorrect -> {
                correctAnswersCounter++
                showAnswerNoteIcons(correctAnswer!!)
                Pair(R.string.correct, R.color.first_green)
            }
            else -> {
                val incorrectAnswer = answers?.find { it.answerName == currentButton.text }
                showAnswerNoteIcons(correctAnswer!!, incorrectAnswer)
                Pair(R.string.incorrect, R.color.first_red)
            }
        }

        title.text = context.getString(strId)
        title.textSize = 26f
        title.setTextColor(context.getColor(colorId))

        currentButton.setBackgroundColor(context.getColor(colorId))
        currentButton.setTextColor(context.getColor(R.color.ultra_light_purple))
    }

    private fun makeControlButtons(context: Context) {
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
                if (!controlButtons.repeatButton.isEnabled && iterationCounter == 10) {
                    when (correctAnswersCounter) {
                        10 -> viewModel.setExerciseResult(args.getInt("CourseId"), args.getInt("CourseItemId"), CourseItemProgressType.COMPLETED)
                        else -> viewModel.setExerciseResult(args.getInt("CourseId"), args.getInt("CourseItemId"), CourseItemProgressType.FAILED)
                    }

                    viewModel.progressSavingResult.observe(viewLifecycleOwner, exerciseSavingResultCOMPLETEObserver)
                    return@setOnClickListener
                }

                if (!controlButtons.repeatButton.isEnabled) {
                    iterationCounter++
                    controlButtons.exerciseCounter.text = getString(R.string.exercise_counter, iterationCounter)
                }

                viewModel.nextIteration(context)
            }

            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            text = context.getString(R.string.skip_btn)
        }

        frameLayout.addView(repeatButton)
        frameLayout.addView(exerciseCounter)
        frameLayout.addView(nextButton)
        controlButtons = ControlButtonsUI(frameLayout, repeatButton, exerciseCounter, nextButton)
    }

    private fun changeButtonsEnabled(isEnabled: Boolean) {
        controlButtons.nextButton.isEnabled = isEnabled
        controlButtons.repeatButton.isEnabled = isEnabled

        for (line in answerButtonsLines) {
            for (btnIndex in 0 until line.childCount) {
                val btn = line.getChildAt(btnIndex) as MaterialButton
                btn.isEnabled = isEnabled
            }
        }
    }

    private fun showNoteIcon(noteString: String?, colorCode: Int = ContextCompat.getColor(requireContext(), R.color.light_gray), shouldScroll: Boolean = false) {
        noteString ?: return

        val octave = noteString.substring(noteString.length - 1)
        val neededPianoView = view?.findViewWithTag<PianoView>(octave)
        val noteIconView = neededPianoView?.findViewWithTag<NoteIconView>(noteString.substring(0, noteString.length - 1) + "icon")
        noteIconView?.visibility = View.VISIBLE
        noteIconView?.noteBackgroundColor = colorCode
        noteIconView?.invalidate()

        if (shouldScroll) {
            val pianoViews = getAllPianoViews(binding.pianoScroll)
            val pianoViewIndex = pianoViews.indexOf(neededPianoView)
            val pianoViewsWidth = pianoViews.take(pianoViewIndex).sumOf { it.width }
            val scrollX = noteIconView?.left?.plus(pianoViewsWidth)?.minus(500) ?: 0
            binding.pianoScroll.smoothScrollTo(scrollX, 0)
        }
    }

    private fun showAnswerNoteIcons(correctAnswer: AnswerModel, incorrectAnswer: AnswerModel? = null) {
        val firstAnswerFirstNote = correctAnswer.firstNoteName
        val secondAnswerFirstNote = correctAnswer.secondNoteName

        showNoteIcon(firstAnswerFirstNote, ContextCompat.getColor(requireContext(), R.color.first_green), true)
        showNoteIcon(secondAnswerFirstNote, ContextCompat.getColor(requireContext(), R.color.first_green))

        if (incorrectAnswer != null) {
            val firstAnswerSecondNote = incorrectAnswer.firstNoteName
            val secondAnswerSecondNote = incorrectAnswer.secondNoteName

            showNoteIcon(firstAnswerSecondNote, ContextCompat.getColor(requireContext(), R.color.deep_coral))
            showNoteIcon(secondAnswerSecondNote, ContextCompat.getColor(requireContext(), R.color.deep_coral))
        }
    }

    private fun invisibleAllNoteIcons() {
        for (octave in 1..7) {
            val neededPianoView = view?.findViewWithTag<PianoView>(octave.toString())
            for (note in 0..11) {
                val noteIconView = neededPianoView?.findViewWithTag<NoteIconView>(viewModel.notes[note] + "_icon")
                noteIconView?.visibility = View.INVISIBLE
            }
        }
    }

    private fun getAllPianoViews(parent: ViewGroup): List<PianoView> {
        val pianoViews = mutableListOf<PianoView>()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is PianoView) {
                pianoViews.add(child)
            } else if (child is ViewGroup) {
                pianoViews.addAll(getAllPianoViews(child))
            }
        }
        return pianoViews
    }
}