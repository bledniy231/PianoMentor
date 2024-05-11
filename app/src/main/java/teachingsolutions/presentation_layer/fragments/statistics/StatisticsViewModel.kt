package teachingsolutions.presentation_layer.fragments.statistics

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.domain_layer.statistics.StatisticsRepository
import teachingsolutions.presentation_layer.fragments.statistics.model.MainMenuItemModelUI
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsResultUI
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val statRepository: StatisticsRepository) : ViewModel() {

    private val _userStatistics = MutableLiveData<StatisticsResultUI?>()
    val userStatstics: LiveData<StatisticsResultUI?> = _userStatistics

    private val _isRefreshingChecked = MutableLiveData<Boolean?>()
    val isRefreshingChecked: LiveData<Boolean?> = _isRefreshingChecked
    fun getMainMenuItems(): List<MainMenuItemModelUI> {
        return listOf(
            MainMenuItemModelUI(R.drawable.icon_cources, "Курсы"),
            MainMenuItemModelUI(R.drawable.icon_practice, "Практические занятия"),
            MainMenuItemModelUI(R.drawable.icon_lectures, "Лекции по теории"),
            MainMenuItemModelUI(R.drawable.icon_tests, "Тесты по теории"),
            MainMenuItemModelUI(R.drawable.icon_hearing, "Тренировка слуха"),
            MainMenuItemModelUI(R.drawable.icon_sound_analyze, "Анализатор звука")
        )
    }

    fun getUserStatistics() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = statRepository.getUserStatistics(userRepository.userId ?: 0)
                _userStatistics.postValue(result)
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.isLoggedIn
    }

    fun refreshUserIfNeeds() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val flag = userRepository.refreshUserIfNeeds()
                _isRefreshingChecked.postValue(flag)
            }
        }
    }

    fun clearStatisticsAfterLogout() {
        _userStatistics.postValue(StatisticsResultUI(null, "Unauthorized"))
        _isRefreshingChecked.postValue(false)
    }

    fun clearLiveData() {
        _userStatistics.postValue(null)
        _isRefreshingChecked.postValue(null)
    }

    fun getObjectAnimator(progressBar: ProgressBar, progressValue: Int): ObjectAnimator {
        val progressAnim = ObjectAnimator.ofInt(progressBar, "progress", 0, progressValue)
        progressAnim.duration = 500
        progressAnim.interpolator = DecelerateInterpolator()
        return progressAnim
    }

    fun getValueAnimator(textView: TextView, textValue: Int, stringResource: Int? = null): ValueAnimator {
        val textValueAnim = ValueAnimator.ofInt(0, textValue)
        textValueAnim.duration = 500

        if (stringResource != null) {
            textValueAnim.addUpdateListener { anim ->
                textView.text = textView.context.getString(stringResource, anim.animatedValue as Float)
            }
        } else {
            textValueAnim.addUpdateListener { anim ->
                textView.text = anim.animatedValue.toString()
            }
        }
        return textValueAnim
    }
}