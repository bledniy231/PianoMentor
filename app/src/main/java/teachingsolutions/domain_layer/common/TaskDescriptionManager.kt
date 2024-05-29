package teachingsolutions.domain_layer.common

import teachingsolutions.domain_layer.domain_models.exercise.ExerciseTypes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDescriptionManager @Inject constructor() {
    private val listOfIntervalsDescriptions = listOf(
        Pair(ExerciseTypes.COMPARISON_ASC, "В данном практическом занятии будут проиграны два восходящих интервала подряд.\nВаша задача - сравнить их и определить, какой из них шире, и выбрать соответствующий вариант ответа"),
        Pair(ExerciseTypes.COMPARISON_DESC, "В данном практическом занятии будут проиграны два нисходящих интервала подряд.\nВаша задача - сравнить их и определить, какой из них шире, и выбрать соответствующий вариант ответа"),
        Pair(ExerciseTypes.DETERMINATION_ASC, "В данном практическом занятии будет проигран один восходящий интервал.\nВаша задача - определить, какой интервал был проигран, и выбрать соответствующий вариант ответа"),
        Pair(ExerciseTypes.DETERMINATION_DESC, "В данном практическом занятии будет проигран один нисходящий интервал.\nВаша задача - определить, какой интервал был проигран, и выбрать соответствующий вариант ответа"),
        Pair(ExerciseTypes.COMPARISON_HARMONIOUS, "В данном практическом занятии будут проиграны два гармонических интервала подряд.\nВаша задача - сравнить их и определить, какой из них шире, и выбрать соответствующий вариант ответа"),
        Pair(ExerciseTypes.DETERMINATION_HARMONIOUS, "В данном практическом занятии будет проигран один гармонический интервал.\nВаша задача - определить, какой интервал был проигран, и выбрать соответствующий вариант ответа"),
        Pair(ExerciseTypes.DETERMINATION_MULTIPLE, "В данном практическом занятии будет проигран один восходящий интервал из некоторой выборки разных интервалов.\nВаша задача - определить, какие интервалы были проиграны, и выбрать соответствующий вариант ответа")
    )

    fun getDescriptionForExerciseType(exerciseType: ExerciseTypes): String {
        return listOfIntervalsDescriptions.find { it.first == exerciseType }?.second ?: ""
    }
}