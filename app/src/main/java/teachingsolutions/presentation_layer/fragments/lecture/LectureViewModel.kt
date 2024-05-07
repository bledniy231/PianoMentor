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
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.statistics.StatisticsRepository
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.lecture.model.LectureAnimation
import teachingsolutions.presentation_layer.fragments.lecture.model.LecturePdfResultUI
import javax.inject.Inject

@HiltViewModel
class LectureViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val userRepository: UserRepository,
    private val statisticsRepository: StatisticsRepository,
    private val sharedPreferences: SharedPreferences,
    private val prefKeys: SharedPreferencesKeys
) : ViewModel() {

    private val _lecturePdfResult = MutableLiveData<LecturePdfResultUI>()
    val lecturePdfResult: LiveData<LecturePdfResultUI> = _lecturePdfResult

    private val _setLectureProgressResult = MutableLiveData<DefaultResponseUI>()
    val setLectureProgressResult: LiveData<DefaultResponseUI> = _setLectureProgressResult

    fun getLecturePdf(courseItemId: Int, courseName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = coursesRepository.getLecturePdfFile(courseItemId, courseName)
                _lecturePdfResult.postValue(result)
            }
        }
    }

    fun deleteLecturePdf(courseItemId: Int, courseName: String) {
        coursesRepository.deleteLecturePdfFile(courseItemId, courseName)
    }

    fun setLectureProgress(courseItemId: Int, progress: CourseItemProgressType) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userId = userRepository.userId ?: 0
                val courseId = coursesRepository.getCourseIdByCourseItemId(courseItemId)
                if (userId == 0L || courseId == 0) return@withContext
                val defaultResponse = statisticsRepository.setCourseItemProgress(userId, courseId, courseItemId, progress)
                coursesRepository.setCourseItemProgress(courseItemId, progress)
                _setLectureProgressResult.postValue(defaultResponse)
            }
        }
    }

    fun saveLectureAnimationSettings(animation: LectureAnimation) {
        sharedPreferences.edit().putString(prefKeys.KEY_LECTURE_ANIMATION, animation.name).apply()
    }

    fun getLectureAnimationSettings(): LectureAnimation {
        val animationName = sharedPreferences.getString(prefKeys.KEY_LECTURE_ANIMATION, LectureAnimation.NONE.name)
        return LectureAnimation.valueOf(animationName ?: LectureAnimation.NONE.name)
    }
}