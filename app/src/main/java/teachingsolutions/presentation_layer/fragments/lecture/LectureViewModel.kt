package teachingsolutions.presentation_layer.fragments.lecture

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.shared_preferences_keys.SharedPreferencesKeys
import teachingsolutions.domain_layer.courses.CoursesRepository
import teachingsolutions.presentation_layer.fragments.lecture.model.LectureAnimation
import teachingsolutions.presentation_layer.fragments.lecture.model.LecturePdfResultUI
import javax.inject.Inject

@HiltViewModel
class LectureViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val sharedPreferences: SharedPreferences,
    private val prefKeys: SharedPreferencesKeys
) : ViewModel() {

    private val _lecturePdfResult = MutableLiveData<LecturePdfResultUI>()
    val lecturePdfResult: LiveData<LecturePdfResultUI> = _lecturePdfResult

    fun getLecturePdf(courseItemId: Int, courseName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = coursesRepository.getLecturePdfFile(courseItemId, courseName)) {
                    is ActionResult.Success -> {
                        _lecturePdfResult.postValue(result.data)
                    }
                    is ActionResult.NormalError -> {
                        _lecturePdfResult.postValue(result.data)
                    }
                    is ActionResult.ExceptionError -> {
                        _lecturePdfResult.postValue(LecturePdfResultUI(null, result.exception.message))
                    }
                }
            }
        }
    }

    fun deleteLecturePdf(courseItemId: Int, courseName: String) {
        coursesRepository.deleteLecturePdfFile(courseItemId, courseName)
    }

    fun saveLectureAnimationSettings(animation: LectureAnimation) {
        sharedPreferences.edit().putString(prefKeys.KEY_LECTURE_ANIMATION, animation.name).apply()
    }

    fun getLectureAnimationSettings(): LectureAnimation {
        val animationName = sharedPreferences.getString(prefKeys.KEY_LECTURE_ANIMATION, LectureAnimation.NONE.name)
        return LectureAnimation.valueOf(animationName ?: LectureAnimation.NONE.name)
    }
}