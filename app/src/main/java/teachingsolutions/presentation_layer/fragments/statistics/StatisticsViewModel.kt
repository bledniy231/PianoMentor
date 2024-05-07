package teachingsolutions.presentation_layer.fragments.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
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

    private val _userStatistics = MutableLiveData<StatisticsResultUI>()
    val userStatstics: LiveData<StatisticsResultUI> = _userStatistics

    private val _isUserStillAvailable = MutableLiveData<Boolean>()
    val isUserStillAvailable: LiveData<Boolean> = _isUserStillAvailable
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

    fun isUserStillAvailable() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val flag = userRepository.checkIfCurrentUserValid()
                _isUserStillAvailable.postValue(flag)
            }
        }
    }
}