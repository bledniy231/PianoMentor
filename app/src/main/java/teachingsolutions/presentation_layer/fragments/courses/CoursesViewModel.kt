package teachingsolutions.presentation_layer.fragments.courses

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import teachingsolutions.domain_layer.mapping_models.CourseRecyclerViewItemModel
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(): ViewModel() {
    fun getCoursesList(): List<CourseRecyclerViewItemModel> {
        return listOf(
            CourseRecyclerViewItemModel("Курс \"Введение\"", "Уровень сложности: лёгкий", "Начальный курс, обязательный к прохождению каждому. \nЗдесь собраны все самые нужные для старта теоретические материалы, а так же начинаются практические задания", 28),
            CourseRecyclerViewItemModel("Курс \"Продолжение\"", "Уровень сложности: средний", "Продолжение начального курса. \nЗдесь вы познакомитесь с новыми темами и углубитесь в уже известные. \nТак же вас ждут новые практические задания", 4),
            CourseRecyclerViewItemModel("Курс \"Профи\"", "Уровень сложности: сложный", "Самый сложный курс, который подойдёт только тем, кто уже прошёл предыдущие курсы. \nЗдесь вы найдёте самые сложные темы и задания", 0)
        )
    }
}