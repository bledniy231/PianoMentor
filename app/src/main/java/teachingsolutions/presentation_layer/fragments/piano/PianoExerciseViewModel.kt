package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.media.MediaPlayer
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import teachingsolutions.presentation_layer.fragments.piano.model.DidIntervalsPlayed
import teachingsolutions.presentation_layer.fragments.piano.model.ExerciseAnswersUI
import teachingsolutions.presentation_layer.fragments.piano.model.ExerciseTaskTextModelUI
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.random.Random

@HiltViewModel
class PianoExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val taskDescManager: TaskDescriptionManager
) : ViewModel() {

    private val _exerciseAnswersUI = MutableLiveData<ExerciseAnswersUI?>()
    val exerciseAnswersUI: LiveData<ExerciseAnswersUI?> = _exerciseAnswersUI

    private val _exerciseTaskTextUI = MutableLiveData<ExerciseTaskTextModelUI?>()
    val exerciseTaskTextUI: LiveData<ExerciseTaskTextModelUI?> = _exerciseTaskTextUI

    private val _didIntervalsPlayed = MutableLiveData<DidIntervalsPlayed?>()
    val didIntervalsPlayed: LiveData<DidIntervalsPlayed?> = _didIntervalsPlayed

    private val notes = listOf("note_c", "note_c_sharp", "note_d", "note_d_sharp", "note_e", "note_f", "note_f_sharp", "note_g", "note_g_sharp", "note_a", "note_a_sharp", "note_b")
    private val allNotes = List(7) { octave -> notes.map { note -> "${note}_${octave+1}" } }.flatten()
    private var intervalsPairsMedia: List<Pair<MediaPlayer, MediaPlayer>>? = null
    private var currentExerciseTaskModel: ExerciseTaskModel? = null

    companion object {
        private const val fadeOutDelay: Long = 10
    }

    fun startFirstIteration(context: Context, courseItemId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val exerciseTaskPair = exerciseRepository.getExerciseTask(courseItemId)
                if (exerciseTaskPair.second != null || exerciseTaskPair.first == null) {
                    val exerciseAnswersUI = ExerciseAnswersUI(null)
                    _exerciseAnswersUI.postValue(exerciseAnswersUI)
                    return@withContext
                }

                currentExerciseTaskModel = exerciseTaskPair.first
                val text = taskDescManager.getTaskName(currentExerciseTaskModel?.exerciseType ?: ExerciseTypes.COMPARISON_ASC)
                val textSize = 20f
                _exerciseTaskTextUI.postValue(ExerciseTaskTextModelUI(text, textSize))
                generateIntervals(context)
            }
        }
    }

    fun playIntervals() {
        viewModelScope.launch {
            _didIntervalsPlayed.postValue(DidIntervalsPlayed(false))
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
                        } else {
                            intervals.first.start()
                            delay(700)
                            MediaPlayerManager.fadeOutSound(intervals.first, fadeOutDelay)
                            intervals.second.start()
                            delay(700)
                            MediaPlayerManager.fadeOutSound(intervals.second, fadeOutDelay)
                        }

                        if (count != intervalsPairs.size - 1) {
                            delay(1000)
                        }
                    }
                }
            }

            _didIntervalsPlayed.postValue(DidIntervalsPlayed(true))
        }
    }


    fun nextIteration(context: Context, title: TextView) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (pair in intervalsPairsMedia ?: emptyList()) {
                    pair.first.release()
                    pair.second.release()
                }
                title.text = taskDescManager.getTaskName(currentExerciseTaskModel?.exerciseType ?: ExerciseTypes.COMPARISON_ASC)
                title.textSize = 20f
                generateIntervals(context)
            }

            playIntervals()
        }
    }




    private fun generateIntervals(context: Context) {
        val exerciseAnswers = when (currentExerciseTaskModel?.exerciseType) {
            ExerciseTypes.COMPARISON_ASC,
            ExerciseTypes.COMPARISON_HARMONIOUS -> {
                val maxIntervalWidth = currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1
                generateCompIntervalsMedia(context, 0, allNotes.size - maxIntervalWidth)
            }
            ExerciseTypes.DETERMINATION_ASC,
            ExerciseTypes.DETERMINATION_HARMONIOUS,
            ExerciseTypes.DETERMINATION_MULTIPLE -> {
                val maxIntervalWidth = currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1
                generateDeterIntervalsMedia(context, 0, allNotes.size - maxIntervalWidth)
            }
            ExerciseTypes.COMPARISON_DESC -> {
                val maxIntervalWidth = -(currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1)
                generateCompIntervalsMedia(context, maxIntervalWidth.absoluteValue, allNotes.size)
            }
            ExerciseTypes.DETERMINATION_DESC -> {
                val maxIntervalWidth = -(currentExerciseTaskModel?.intervalsInTask?.maxOf { it.width } ?: 1)
                generateDeterIntervalsMedia(context, maxIntervalWidth.absoluteValue, allNotes.size)
            }
            null -> { null }
        }

        _exerciseAnswersUI.postValue(exerciseAnswers?.let { ExerciseAnswersUI(it) })
    }


    private fun generateCompIntervalsMedia(context: Context,
                                           startIndex: Int,
                                           endIndex: Int): List<Pair<String, Boolean>> {
        currentExerciseTaskModel ?: return emptyList()
        val currentNoteIndex = Random.nextInt(startIndex, endIndex)
        val currentCorrectInterval = Random.nextInt(0,2)

        val answers: List<Pair<String, Boolean>>
        val sortedIntervals = if (currentCorrectInterval == 0) {
            answers = listOf(
                Pair("Первый", true),
                Pair("Второй", false)
            )
            currentExerciseTaskModel?.intervalsInTask?.sortedByDescending { it.width }
        } else {
            answers = listOf(
                Pair("Первый", false),
                Pair("Второй", true)
            )
            currentExerciseTaskModel?.intervalsInTask?.sortedBy { it.width }
        }
        sortedIntervals ?: return emptyList()

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

        return answers
    }


    private fun generateDeterIntervalsMedia(context: Context,
                                            startIndex: Int,
                                            endIndex: Int): List<Pair<String, Boolean>> {
        currentExerciseTaskModel ?: return emptyList()
        val currentNoteIndex = Random.nextInt(startIndex, endIndex)
        val correctIntervalIndex = Random.nextInt(0, currentExerciseTaskModel?.intervalsInTask!!.size)
        val answers = currentExerciseTaskModel?.intervalsInTask!!.map {
            Pair(it.russianTranslation, it == currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex])
        }

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

        return answers
    }


    override fun onCleared() {
        super.onCleared()
        for (pair in intervalsPairsMedia ?: emptyList()) {
            pair.first.release()
            pair.second.release()
        }
        intervalsPairsMedia = null
        currentExerciseTaskModel = null
    }
}