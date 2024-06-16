package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.common.TaskDescriptionManager
import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTaskModel
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTypes
import teachingsolutions.domain_layer.exercise.ExerciseRepository
import teachingsolutions.domain_layer.common.MediaPlayerManager
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.piano.model.AnswerModel
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

    private val _progressSavingResult = MutableLiveData<DefaultResponseUI?>()
    val progressSavingResult: LiveData<DefaultResponseUI?> = _progressSavingResult

    val notes = listOf("note_c", "note_c_sharp", "note_d", "note_d_sharp", "note_e", "note_f", "note_f_sharp", "note_g", "note_g_sharp", "note_a", "note_a_sharp", "note_b")
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
                _exerciseTaskTextUI.postValue(ExerciseTaskTextModelUI(text, textSize, context.getColor(R.color.dark_gray)))
                generateIntervals(context)
            }
        }
    }

    fun playIntervals() {
        viewModelScope.launch {
            _didIntervalsPlayed.postValue(DidIntervalsPlayed(false))

            withContext(Dispatchers.IO) {
                intervalsPairsMedia?.let { intervalsPairs ->
                    for ((count, intervalNotes) in intervalsPairs.withIndex()) {
                        if (currentExerciseTaskModel?.exerciseType == ExerciseTypes.COMPARISON_HARMONIOUS
                            || currentExerciseTaskModel?.exerciseType == ExerciseTypes.DETERMINATION_HARMONIOUS) {
                            intervalNotes.first.start()
                            intervalNotes.second.start()
                            delay(1000)
                            MediaPlayerManager.fadeOutSound(intervalNotes.first, fadeOutDelay)
                            MediaPlayerManager.fadeOutSound(intervalNotes.second, fadeOutDelay)
                        } else {
                            intervalNotes.first.start()
                            delay(700)
                            MediaPlayerManager.fadeOutSound(intervalNotes.first, fadeOutDelay)
                            intervalNotes.second.start()
                            delay(700)
                            MediaPlayerManager.fadeOutSound(intervalNotes.second, fadeOutDelay)
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


    fun nextIteration(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (intervalNotes in intervalsPairsMedia ?: emptyList()) {
                    intervalNotes.first.pause()
                    intervalNotes.second.pause()
                    intervalNotes.first.stop()
                    intervalNotes.second.stop()
                    intervalNotes.first.release()
                    intervalNotes.second.release()
                }
                val text = taskDescManager.getTaskName(currentExerciseTaskModel?.exerciseType ?: ExerciseTypes.COMPARISON_ASC)
                val textSize = 20f
                _exerciseTaskTextUI.postValue(ExerciseTaskTextModelUI(text, textSize, context.getColor(R.color.dark_gray)))
                generateIntervals(context)
            }

            playIntervals()
        }
    }

    fun setExerciseResult(courseId: Int, courseItemId: Int, exerciseProgress: CourseItemProgressType) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = exerciseRepository.setExerciseProgress(courseId, courseItemId, exerciseProgress)
                _progressSavingResult.postValue(result)
            }
        }
    }

    fun calculateExerciseResults(iterationCounter: Int, correctAnswersCounter: Int): Triple<Int, Int, Double> {
        val correctAnswersPercentage = if (iterationCounter > 0) {
            (correctAnswersCounter.toDouble() / iterationCounter) * 100
        } else {
            0.0
        }
        return Triple(iterationCounter, correctAnswersCounter, correctAnswersPercentage)
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
                                           endIndex: Int): List<AnswerModel> {
        currentExerciseTaskModel ?: return emptyList()

        val currentNoteIndex = Random.nextInt(startIndex, endIndex)
        val currentCorrectInterval = Random.nextInt(0,2)

        val answers: List<AnswerModel>
        val sortedIntervals = if (currentCorrectInterval == 0) {
            answers = listOf(
                AnswerModel("Первый", true),
                AnswerModel("Второй", false)
            )
            currentExerciseTaskModel?.intervalsInTask?.sortedByDescending { it.width }
        } else {
            answers = listOf(
                AnswerModel("Первый", false),
                AnswerModel("Второй", true)
            )
            currentExerciseTaskModel?.intervalsInTask?.sortedBy { it.width }
        }
        sortedIntervals ?: return emptyList()

        val result: MutableList<Pair<MediaPlayer, MediaPlayer>> = mutableListOf()

        for ((count, interval) in sortedIntervals.withIndex()) {
            val firstNoteId = context.resources.getIdentifier(allNotes[currentNoteIndex], "raw", context.packageName)

            val secondNoteIndex = if (startIndex == 0) {
                currentNoteIndex + interval.width
            } else {
                currentNoteIndex - interval.width
            }
            val secondNoteId = context.resources.getIdentifier(allNotes[secondNoteIndex], "raw", context.packageName)

            answers[count].firstNoteName = allNotes[currentNoteIndex]
            answers[count].secondNoteName = allNotes[secondNoteIndex]

            result.add(Pair(MediaPlayer.create(context, firstNoteId), MediaPlayer.create(context, secondNoteId)))
        }

        intervalsPairsMedia = result

        return answers
    }


    private fun generateDeterIntervalsMedia(context: Context,
                                            startIndex: Int,
                                            endIndex: Int): List<AnswerModel> {
        currentExerciseTaskModel ?: return emptyList()

        val currentNoteIndex = Random.nextInt(startIndex, endIndex)
        val correctIntervalIndex = Random.nextInt(0, currentExerciseTaskModel?.intervalsInTask!!.size)
        val answers = currentExerciseTaskModel?.intervalsInTask!!.map {
            AnswerModel(
                it.russianTranslation,
                it == currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex],
                allNotes[currentNoteIndex],
                if (startIndex == 0) {
                    allNotes[currentNoteIndex + it.width]
                } else {
                    allNotes[currentNoteIndex - it.width]
                })
        }

        val result: MutableList<Pair<MediaPlayer, MediaPlayer>> = mutableListOf()

        val firstNoteId = context.resources.getIdentifier(allNotes[currentNoteIndex], "raw", context.packageName)

        val secondNoteIndex = if (startIndex == 0) {
            currentNoteIndex + currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex].width
        } else {
            currentNoteIndex - currentExerciseTaskModel?.intervalsInTask!![correctIntervalIndex].width
        }
        val secondNoteId = context.resources.getIdentifier(allNotes[secondNoteIndex], "raw", context.packageName)



        result.add(Pair(MediaPlayer.create(context, firstNoteId), MediaPlayer.create(context, secondNoteId)))

        intervalsPairsMedia = result

        return answers
    }


    override fun onCleared() {
        super.onCleared()
        for (intervalNotes in intervalsPairsMedia ?: emptyList()) {
            intervalNotes.first.pause()
            intervalNotes.second.pause()
            intervalNotes.first.stop()
            intervalNotes.second.stop()
            intervalNotes.first.release()
            intervalNotes.second.release()
        }
        intervalsPairsMedia = null
        currentExerciseTaskModel = null
    }
}