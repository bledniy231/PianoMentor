package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.widget.LinearLayout.LayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.domain_models.exercise.Intervals
import teachingsolutions.domain_layer.exercise.ExerciseRepository
import teachingsolutions.presentation_layer.fragments.piano.model.GetExerciseTaskResponseUI
import javax.inject.Inject

@HiltViewModel
class EnterPianoExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
): ViewModel() {

    private val _exerciseTask = MutableLiveData<GetExerciseTaskResponseUI?>()
    val exerciseTask: LiveData<GetExerciseTaskResponseUI?> = _exerciseTask

    fun getExerciseTask(courseItemId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = exerciseRepository.getExerciseTask(courseItemId)
                _exerciseTask.postValue(result)
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
                // Do something
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
}