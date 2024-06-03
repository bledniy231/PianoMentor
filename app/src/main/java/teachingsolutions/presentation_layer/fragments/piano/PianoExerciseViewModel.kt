package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.exercise.ExerciseRepository
import javax.inject.Inject

@HiltViewModel
class PianoExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    fun getIntervalsForPlaying() {

    }

    fun getButtons(context: Context): List<MaterialButton> {
        val result: MutableList<MaterialButton> = mutableListOf()
        for ((count, answer) in answers.withIndex()) {
            val button = MaterialButton(context)
            button.text = answer.russianTranslation
            button.id = count + 1

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                getPixelsFromDp(context, 40)
            )
            val paddingHorizontal = getPixelsFromDp(context, 5)
            button.setPadding(paddingHorizontal, button.paddingTop, paddingHorizontal, button.paddingBottom)
            button.cornerRadius = getPixelsFromDp(context, 10)

            button.setOnClickListener {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        if (interval.ordinal < EnterPianoExerciseViewModel.notes.size && EnterPianoExerciseViewModel.notes[0].second != null && EnterPianoExerciseViewModel.notes[interval.width].second != null) {
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
}