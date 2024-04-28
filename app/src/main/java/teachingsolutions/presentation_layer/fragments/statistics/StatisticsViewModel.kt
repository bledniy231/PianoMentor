package teachingsolutions.presentation_layer.fragments.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import teachingsolutions.domain_layer.login_registration.UserRepository
import teachingsolutions.domain_layer.mapping_models.statistics.CoursesProgressModel
import teachingsolutions.domain_layer.mapping_models.statistics.ExercisesProgressModel
import teachingsolutions.domain_layer.mapping_models.statistics.LecturesProgressModel
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    fun getMainMenuItems(): List<MainMenuItemModelUI> {
        return listOf(
            MainMenuItemModelUI(R.drawable.icon_cources, "Курсы"),
            MainMenuItemModelUI(R.drawable.icon_practice, "Практические занятия"),
            MainMenuItemModelUI(R.drawable.icon_lectures, "Лекции по теории"),
            MainMenuItemModelUI(R.drawable.icon_lectures_tests, "Тесты по теории"),
            MainMenuItemModelUI(R.drawable.icon_hearing, "Тренировка слуха"),
            MainMenuItemModelUI(R.drawable.icon_sound_analyze, "Анализатор звука")
        )
    }

    fun getUserStatistics(): UserStatisticsModel {
        val statListViewPagerItems: List<StatisticsViewPagerItemModelUI> = listOf(
            StatisticsViewPagerItemModelUI(1,10, "Выполнено тестов", "Вы прошли 1 тест по теории, продолжайте в том же духе"),
            StatisticsViewPagerItemModelUI(1, 33, "Завершено курсов", "Вы завершили 1 курс, это большой шаг!")
        )

        val exercisesProgressModel = ExercisesProgressModel(21, 90, "Упражнение")
        val lecturesProgressModel = LecturesProgressModel(3, 33, "Лекции")
        val coursesProgressModel = CoursesProgressModel(1, 100, "Курсы")

        return UserStatisticsModel(statListViewPagerItems, exercisesProgressModel, lecturesProgressModel, coursesProgressModel)
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.isLoggedIn
    }

    fun isUserStillAvailable(): Boolean {
        var result: Boolean = false
        viewModelScope.launch {
            with(Dispatchers.IO) {
                result = userRepository.checkIfCurrentUserValid()
            }
        }
        return result
    }

}