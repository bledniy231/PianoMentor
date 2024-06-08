package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.common.PixelLengthManager
import teachingsolutions.domain_layer.common.TaskDescriptionManager
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTaskModel
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTypes
import teachingsolutions.domain_layer.exercise.ExerciseRepository
import teachingsolutions.domain_layer.exercise.MediaPlayerManager
import teachingsolutions.presentation_layer.fragments.piano.model.ControlButtonsUI
import teachingsolutions.presentation_layer.fragments.piano.model.ExerciseModelUI
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.random.Random

@HiltViewModel
class PianoExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val taskDescManager: TaskDescriptionManager
) : ViewModel() {

    private val _controlButtons = MutableLiveData<ControlButtonsUI?>()
    val controlButtons: LiveData<ControlButtonsUI?> = _controlButtons

    private val _exerciseModelUI = MutableLiveData<ExerciseModelUI?>()
    val exerciseModelUI: LiveData<ExerciseModelUI?> = _exerciseModelUI

    private val notes = listOf("note_c", "note_c_sharp", "note_d", "note_d_sharp", "note_e", "note_f", "note_f_sharp", "note_g", "note_g_sharp", "note_a", "note_a_sharp", "note_b")
    private val allNotes = List(7) { octave -> notes.map { note -> "${note}_${octave+1}" } }.flatten()
    private var intervalsPairsMedia: List<Pair<MediaPlayer, MediaPlayer>>? = null
    private var currentExerciseTaskModel: ExerciseTaskModel? = null
    private var buttonLines: MutableList<LinearLayout>? = null
    private var iterationCounter: Int = 1

    companion object {
        private const val fadeOutDelay: Long = 10
    }

    fun getControlButtons(context: Context, title: TextView) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cntrlBtns = makeControlButtons(context, title)
                _controlButtons.postValue(cntrlBtns)
            }
        }
    }

    fun startFirstIteration(context: Context, courseItemId: Int, title: TextView) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val exerciseTaskPair = exerciseRepository.getExerciseTask(courseItemId)
                if (exerciseTaskPair.second == null || exerciseTaskPair.first == null) {
                    val exerciseModelUI = ExerciseModelUI(null)
                    _exerciseModelUI.postValue(exerciseModelUI)
                }

                currentExerciseTaskModel = exerciseTaskPair.first
                title.text = taskDescManager.getTaskName(currentExerciseTaskModel?.exerciseType ?: ExerciseTypes.COMPARISON_ASC)
                generateIntervals(context, title, true)
            }
        }
    }

    fun playIntervals() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                intervalsPairsMedia?.let { intervalsPairs ->
                    for ((count, intervals) in intervalsPairs.withIndex()) {
                        if (currentExerciseTaskModel?.exerciseType == ExerciseTypes.COMPARISON_HARMONIOUS
                            || currentExerciseTaskModel?.exerciseType == ExerciseTypes.DETERMINATION_HARMONIOUS) {
                            intervals.first.start()
                            intervals.second.start()
                            delay(1000)
                            MediaPlayerManager.fadeOutSound(intervals.first, fadeOutDelay)
                            MediaPlayerManager.fadeOutSound(intervals.second, fadeOutDelay)
                            if (count != intervalsPairs.size - 1) {
                                delay(1000)
                            }
                        } else {
                            intervals.first.start()
                            delay(700)
                            MediaPlayerManager.fadeOutSound(intervals.first, fadeOutDelay)
                            intervals.second.start()
                            delay(700)
                            MediaPlayerManager.fadeOutSound(intervals.second, fadeOutDelay)
                            if (count != intervalsPairs.size - 1) {
                                delay(1000)
                            }
                        }
                    }


                }
            }
        }
    }

    fun nextIteration(context: Context, title: TextView) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                iterationCounter++
                for (pair in intervalsPairsMedia ?: emptyList()) {
                    pair.first.release()
                    pair.second.release()
                }
                title.text = taskDescManager.getTaskName(currentExerciseTaskModel?.exerciseType ?: ExerciseTypes.COMPARISON_ASC)
                generateIntervals(context, title, false)
            }
        }
    }




    private fun generateIntervals(context: Context, title: TextView, isFirstIteration: Boolean) {
        when (currentExerciseTaskModel?.exerciseType) {
            ExerciseTypes.COMPARISON_ASC,
            ExerciseTypes.COMPARISON_HARMONIOUS -> {
                val maxIntervalWidth = currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1
                generateCompIntervalsMediaAndSetButtons(context, title, 0, allNotes.size - maxIntervalWidth, isFirstIteration)
            }
            ExerciseTypes.DETERMINATION_ASC,
            ExerciseTypes.DETERMINATION_HARMONIOUS,
            ExerciseTypes.DETERMINATION_MULTIPLE -> {
                val maxIntervalWidth = currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1
                generateDeterIntervalsMediaAndSetButtons(context, title,0, allNotes.size - maxIntervalWidth, isFirstIteration)
            }
            ExerciseTypes.COMPARISON_DESC -> {
                val maxIntervalWidth = -(currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1)
                generateCompIntervalsMediaAndSetButtons(context, title, maxIntervalWidth.absoluteValue, allNotes.size, isFirstIteration)
            }
            ExerciseTypes.DETERMINATION_DESC -> {
                val maxIntervalWidth = -(currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1)
                generateDeterIntervalsMediaAndSetButtons(context, title, maxIntervalWidth.absoluteValue, allNotes.size, isFirstIteration)
            }
            null -> {}
        }

        _exerciseModelUI.postValue(ExerciseModelUI(buttonLines))
    }


    private fun generateCompIntervalsMediaAndSetButtons(context: Context,
                                                        title: TextView,
                                                        startIndex: Int,
                                                        endIndex: Int,
                                                        isFirstIteration: Boolean) {
        currentExerciseTaskModel ?: return
        val currentNoteIndex = Random.nextInt(startIndex, endIndex)
        val currentCorrectInterval = Random.nextInt(0,2)

        val sortedIntervals = if (currentCorrectInterval == 0) {
            val answers = listOf(
                Pair("Первый", true),
                Pair("Второй", false)
            )
            setButtonsLines(context, title, answers, isFirstIteration)
            currentExerciseTaskModel?.intervalsInTask?.sortedByDescending { it.width }
        } else {
            val answers = listOf(
                Pair("Первый", false),
                Pair("Второй", true)
            )
            setButtonsLines(context, title, answers, isFirstIteration)
            currentExerciseTaskModel?.intervalsInTask?.sortedBy { it.width }
        }
        sortedIntervals ?: return

        val result: MutableList<Pair<MediaPlayer, MediaPlayer>> = mutableListOf()

        for (interval in sortedIntervals) {
            val firstNoteId = context.resources.getIdentifier(allNotes[currentNoteIndex], "raw", context.packageName)

            val secondNotePosition = if (startIndex == 0) {
                currentNoteIndex + interval.width
            } else {
                currentNoteIndex - interval.width
            }
            val secondNoteId = context.resources.getIdentifier(allNotes[secondNotePosition], "raw", context.packageName)
            result.add(Pair(MediaPlayer.create(context, firstNoteId), MediaPlayer.create(context, secondNoteId)))
        }

        intervalsPairsMedia = result
    }


    private fun generateDeterIntervalsMediaAndSetButtons(context: Context,
                                                         title: TextView,
                                                         startIndex: Int,
                                                         endIndex: Int,
                                                         isFirstIteration: Boolean) {
        currentExerciseTaskModel ?: return
        val currentNoteIndex = Random.nextInt(startIndex, endIndex)
        val correctIntervalIndex = Random.nextInt(0, currentExerciseTaskModel?.intervalsInTask!!.size)
        val answers = currentExerciseTaskModel?.intervalsInTask!!.map {
            Pair(it.russianTranslation, it == currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex])
        }
        setButtonsLines(context, title, answers, isFirstIteration)

        val result: MutableList<Pair<MediaPlayer, MediaPlayer>> = mutableListOf()

        val firstNoteId = context.resources.getIdentifier(allNotes[currentNoteIndex], "raw", context.packageName)
        val secondNotePosition = if (startIndex == 0) {
            currentNoteIndex + currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex].width
        } else {
            currentNoteIndex - currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex].width
        }
        val secondNoteId = context.resources.getIdentifier(allNotes[secondNotePosition], "raw", context.packageName)
        result.add(Pair(MediaPlayer.create(context, firstNoteId), MediaPlayer.create(context, secondNoteId)))

        intervalsPairsMedia = result
    }


    private fun setButtonsLines(context: Context,
                                title: TextView,
                                answers: List<Pair<String, Boolean>>,
                                isFirstIteration: Boolean) {
        if (isFirstIteration) {
            buttonLines = mutableListOf()
        } else {
            for (line in buttonLines ?: emptyList()) {
                for (btnIndex in 0 until line.childCount) {
                    val button = line.getChildAt(btnIndex) as MaterialButton
                    button.isClickable = true
                    button.setBackgroundColor(context.getColor(R.color.first_purple))
                    button.text = answers[(button.tag as Int)].first
                    button.setOnClickListener {
                        onClickListenerChooseBtns(context, title, button, answers[(button.tag as Int)].second)
                    }
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
                }
                buttonLines?.add(layout)
            }

            val button = MaterialButton(context).apply {
                text = answer.first
                tag = count + 1
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    PixelLengthManager.getPixelsFromDp(context, 40)
                )
                val margin = PixelLengthManager.getPixelsFromDp(context, 5)
                (params as ViewGroup.MarginLayoutParams).setMargins(margin, 0, margin, 0)
                textSize = PixelLengthManager.getPixelsFromSp(context, 18f)
                setOnClickListener {
                    onClickListenerChooseBtns(context, title, this, answer.second)
                }
                layoutParams = params
            }

            buttonLines?.last()?.addView(button)
        }
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
        currentButton.setBackgroundColor(context.getColor(colorId))

        for (line in buttonLines ?: emptyList()) {
            for (btnIndex in 0 until line.childCount) {
                val btn = line.getChildAt(btnIndex) as MaterialButton
                btn.isClickable = false
                btn.setBackgroundColor(context.getColor(R.color.light_gray))
            }
        }
    }

    private fun makeControlButtons(context: Context, title: TextView): ControlButtonsUI {
        val frameLayout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.actionBarSize)
            ).apply {
                gravity = Gravity.BOTTOM
                setPadding(
                    PixelLengthManager.getPixelsFromDp(context, 16),
                    0,
                    PixelLengthManager.getPixelsFromDp(context, 16),
                    0
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
                playIntervals()
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
                nextIteration(context, title)

            }
            isClickable = false
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            text = context.getString(R.string.skip_btn)
        }

        frameLayout.addView(repeatButton)
        frameLayout.addView(exerciseCounter)
        frameLayout.addView(nextButton)
        return ControlButtonsUI(frameLayout, repeatButton, exerciseCounter, nextButton)
    }


    override fun onCleared() {
        super.onCleared()
        currentExerciseTaskModel = null
        buttonLines = null
        for (pair in intervalsPairsMedia ?: emptyList()) {
            pair.first.release()
            pair.second.release()
        }
        intervalsPairsMedia = null
    }
}