package teachingsolutions.presentation_layer.fragments.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.domain_layer.courses.CoursesRepository
import teachingsolutions.domain_layer.mapping_models.courses.CourseModel
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemsResult
import teachingsolutions.presentation_layer.fragments.courses.model.CourseModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CoursesResult
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository
): ViewModel() {
    private val _coursesResult = MutableLiveData<CoursesResult>()
    val coursesResult: LiveData<CoursesResult> = _coursesResult

    private val _courseItemsResult = MutableLiveData<CourseItemsResult>()
    val courseItemsResult: LiveData<CourseItemsResult> = _courseItemsResult

    fun getCoursesList(userId: Long) {
        viewModelScope.launch {
            with(Dispatchers.IO) {
                when (val result = coursesRepository.getCourses((userId))) {
                    is ActionResult.Success -> {
                        _coursesResult.postValue(result.data)
                    }
                    is ActionResult.NormalError -> {
                        _coursesResult.postValue(result.data)
                    }
                    is ActionResult.ExceptionError -> {
                        _coursesResult.postValue(CoursesResult(null, result.exception.message))
                    }
                }
            }
        }

    }

    fun getIntroductionCourseItemsList(userId: Long, courseId: Int) {
        viewModelScope.launch {
            with(Dispatchers.IO) {
                when (val result = coursesRepository.getCourseItems(userId, courseId)) {
                    is ActionResult.Success -> {
                        _courseItemsResult.postValue(result.data)
                    }
                    is ActionResult.NormalError -> {
                        _courseItemsResult.postValue(result.data)
                    }
                    is ActionResult.ExceptionError -> {
                        _courseItemsResult.postValue(CourseItemsResult(null, result.exception.message))
                    }
                }
            }
        }
    }
}