package teachingsolutions.presentation_layer.fragments.ui.statistics

import androidx.lifecycle.ViewModel
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.data.main_menu.model.MainMenuRecyclerViewItemModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.CoursesProgressModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.ExercisesProgressModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.LecturesProgressModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.StatisticsViewPagerItemModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.UserStatisticsModel

class StatisticsViewModel : ViewModel() {
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
}