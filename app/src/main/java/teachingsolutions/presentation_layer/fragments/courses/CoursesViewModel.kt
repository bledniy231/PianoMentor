package teachingsolutions.presentation_layer.fragments.courses

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemType
import teachingsolutions.domain_layer.mapping_models.courses.CourseModel
import teachingsolutions.domain_layer.mapping_models.courses.InnerCourseItemModel
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(): ViewModel() {
    fun getCoursesList(): List<CourseModel> {
        return listOf(
            CourseModel(1,"Курс \"Введение\"", "Уровень сложности: лёгкий", "Начальный курс, обязательный к прохождению каждому. \n" +
                    "Здесь собраны все самые нужные для старта теоретические материалы, а так же начинаются практические задания", 28),
            CourseModel(2,"Курс \"Продолжение\"", "Уровень сложности: средний", "Продолжение начального курса. \nЗдесь вы познакомитесь с новыми темами и углубитесь в уже известные. \nТак же вас ждут новые практические задания", 4),
            CourseModel(3,"Курс \"Профи\"", "Уровень сложности: сложный", "Самый сложный курс, который подойдёт только тем, кто уже прошёл предыдущие курсы. \nЗдесь вы найдёте самые сложные темы и задания", 0)
        )
    }

    fun getIntroductionCourseItemsList(): List<InnerCourseItemModel> {
        return listOf(
            InnerCourseItemModel(1, "Начальная теория музыки", CourseItemType.LECTURE, Cou),
            InnerCourseItemModel(2, "Ещё немного теории", CourseItemType.QUIZ, "Пройдено 28%")
        )
    }

    fun getContinuationCourseItemsList(): List<CourseModel>? {

    }

    fun getProfiCourseItemsList(): List<CourseModel>? {

    }
}