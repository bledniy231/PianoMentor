package teachingsolutions.presentation_layer.fragments.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.domain_layer.courses.CoursesRepository
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemsResultUI
import teachingsolutions.presentation_layer.fragments.courses.model.CoursesResultUI
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _coursesResult = MutableLiveData<CoursesResultUI>()
    val coursesResult: LiveData<CoursesResultUI> = _coursesResult

    private val _courseItemsResult = MutableLiveData<CourseItemsResultUI>()
    val courseItemsResult: LiveData<CourseItemsResultUI> = _courseItemsResult

    fun getCoursesList(userId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = coursesRepository.getCourses((userId))
                _coursesResult.postValue(result)
            }
        }

    }

    fun getExactCourseItemsList(userId: Long, courseId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = coursesRepository.getCourseItems(userId, courseId)
                _courseItemsResult.postValue(result)
            }
        }
    }

    fun getUserId(): Long? {
        var userId: Long? = null
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                 userId = userRepository.userId
            }
        }

        return userId
    }
}
