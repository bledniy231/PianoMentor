package teachingsolutions.presentation_layer.fragments.statistics

import androidx.lifecycle.ViewModel
import com.example.pianomentor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import teachingsolutions.data_access_layer.login_registration.UserRepository
import teachingsolutions.domain_layer.mapping_models.MainMenuRecyclerViewItemModel
import teachingsolutions.domain_layer.mapping_models.statistics.CoursesProgressModel
import teachingsolutions.domain_layer.mapping_models.statistics.ExercisesProgressModel
import teachingsolutions.domain_layer.mapping_models.statistics.LecturesProgressModel
import teachingsolutions.domain_layer.mapping_models.statistics.StatisticsViewPagerItemModel
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    fun getMainMenuItems(): List<MainMenuRecyclerViewItemModel> {
        return listOf(
            MainMenuRecyclerViewItemModel(R.drawable.icon_cources, "Курсы"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_practice, "Практические занятия"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_lectures, "Лекции по теории"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_lectures_tests, "Тесты по теории"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_hearing, "Тренировка слуха"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_sound_analyze, "Анализатор звука")
        )
    }

    fun getUserStatistics(): UserStatisticsModel {
        val statListViewPagerItems: List<StatisticsViewPagerItemModel> = listOf(
            StatisticsViewPagerItemModel(1,10, "Выполнено тестов", "Вы прошли 1 тест по теории, продолжайте в том же духе"),
            StatisticsViewPagerItemModel(1, 33, "Завершено курсов", "Вы завершили 1 курс, это большой шаг!")
        )

        val exercisesProgressModel = ExercisesProgressModel(21, 90, "Упражнение")
        val lecturesProgressModel = LecturesProgressModel(3, 33, "Лекции")
        val coursesProgressModel = CoursesProgressModel(1, 100, "Курсы")

        return UserStatisticsModel(statListViewPagerItems, exercisesProgressModel, lecturesProgressModel, coursesProgressModel)
    }

    fun isUserLoggedIn(): Boolean {
        return  userRepository.isLoggedIn
    }
}