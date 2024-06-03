package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.LinearLayout.LayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTaskModel
import teachingsolutions.domain_layer.domain_models.exercise.Intervals
import teachingsolutions.domain_layer.exercise.ExerciseRepository
import teachingsolutions.presentation_layer.fragments.piano.model.ExerciseTaskUI
import teachingsolutions.presentation_layer.fragments.piano.model.GetExerciseTaskResponseUI
import javax.inject.Inject

@HiltViewModel
class EnterPianoExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
): ViewModel() {

    companion object {
        private val notes: MutableList<Pair<String, MediaPlayer?>> = mutableListOf(
            "note_c" to null,
            "note_c_sharp" to null,
            "note_d" to null,
            "note_d_sharp" to null,
            "note_e" to null,
            "note_f" to null,
            "note_f_sharp" to null,
            "note_g" to null,
            "note_g_sharp" to null,
            "note_a" to null,
            "note_a_sharp" to null,
            "note_b" to null
        )
        private const val octave = "4"
    }

    private val _exerciseTask = MutableLiveData<GetExerciseTaskResponseUI?>()
    val exerciseTask: LiveData<GetExerciseTaskResponseUI?> = _exerciseTask
    private var exerciseTaskModel: ExerciseTaskModel? = null

    fun getExerciseTask(context: Context, courseItemId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = exerciseRepository.getExerciseTask(courseItemId)
                exerciseTaskModel = result.first

                val uiResponse = GetExerciseTaskResponseUI(result.first?.let { ExerciseTaskUI(it.exerciseTaskId, it.taskDescription, it.intervalsInTask) }, result.second)
                _exerciseTask.postValue(uiResponse)

                for ((count, noteWithSound) in notes.withIndex()) {
                    noteWithSound.second ?: continue

                    val noteId = context.resources.getIdentifier(noteWithSound.first + "_$octave", "raw", context.packageName)
                    notes[count] = Pair(notes[count].first, MediaPlayer.create(context, noteId))
                }
            }
        }
    }

    fun getButtons(context: Context, intervals: List<Intervals>): List<MaterialButton> {
        val result: MutableList<MaterialButton> = mutableListOf()
        for ((count, interval) in intervals.withIndex()) {
            val button = MaterialButton(context)
            button.text = interval.russianTranslation
            button.id = count + 1

            val params = LayoutParams(LayoutParams.WRAP_CONTENT, getPixelsFromDp(context, 40))
            val paddingHorizontal = getPixelsFromDp(context, 5)
            button.setPadding(paddingHorizontal, button.paddingTop, paddingHorizontal, button.paddingBottom)
            button.cornerRadius = getPixelsFromDp(context, 10)

            button.setOnClickListener {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        if (interval.ordinal < notes.size && notes[0].second != null && notes[interval.width].second != null) {
                            result.forEach { it.isClickable = false }
                            playInterval(interval)
                            result.forEach { it.isClickable = true }
                        }
                    }
                }
            }
            button.layoutParams = params

            result.add(button)
        }

        return result
    }

    private fun getPixelsFromDp(context: Context, dpValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        val pixelValue = (dpValue * scale + 0.5f).toInt()
        return pixelValue
    }

    private suspend fun playInterval(interval: Intervals) {
        notes[0].second?.start()
        delay(1000)
        fadeOutSound(notes[0].second)
        notes[interval.width].second?.start()
        delay(1000)
        fadeOutSound(notes[interval.width].second)
    }

    private suspend fun fadeOutSound(sound: MediaPlayer?) {
        sound ?: return

        var volume = 1.0f
        while (volume > 0) {
            volume -= 0.05f
            if (volume < 0) {
                volume = 0f
            }
            sound.setVolume(volume, volume)
            delay(10)
        }
        sound.pause()
        sound.seekTo(0)
        sound.setVolume(1f, 1f)
    }
}