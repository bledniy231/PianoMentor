package teachingsolutions.domain_layer.domain_models.exercise

enum class Intervals(val value: String, val russianTranslation: String) {
    MINOR_SECOND("minorSecond", "Малая секунда"),
    MAJOR_SECOND("majorSecond", "Большая секунда"),
    MINOR_THIRD("minorThird", "Малая терция"),
    MAJOR_THIRD("majorThird", "Большая терция"),
    PERFECT_FOURTH("perfectFourth", "Чистая кварта"),
    TRITONE("tritone", "Тритон"),
    PERFECT_FIFTH("perfectFifth", "Чистая квинта"),
    MINOR_SIXTH("minorSixth", "Малая секста"),
    MAJOR_SIXTH("majorSixth", "Большая секста"),
    MINOR_SEVENTH("minorSeventh", "Малая септима"),
    MAJOR_SEVENTH("majorSeventh", "Большая септима"),
    OCTAVE("octave", "Октава");

    companion object {
        fun from(value: String?): Intervals = entries.find { it.value == value } ?: MINOR_SECOND
        fun from(id: Int): Intervals = entries.find { it.ordinal == id - 1} ?: MINOR_SECOND
    }
}