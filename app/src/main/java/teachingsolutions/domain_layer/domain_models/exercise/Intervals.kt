package teachingsolutions.domain_layer.domain_models.exercise

enum class Intervals(val value: String, val russianTranslation: String, val width: Int) {
    MINOR_SECOND("minorSecond", "Малая секунда", 1),
    MAJOR_SECOND("majorSecond", "Большая секунда", 2),
    MINOR_THIRD("minorThird", "Малая терция", 3),
    MAJOR_THIRD("majorThird", "Большая терция", 4),
    PERFECT_FOURTH("perfectFourth", "Чистая кварта", 5),
    TRITONE("tritone", "Тритон", 6),
    PERFECT_FIFTH("perfectFifth", "Чистая квинта", 7),
    MINOR_SIXTH("minorSixth", "Малая секста", 8),
    MAJOR_SIXTH("majorSixth", "Большая секста", 9),
    MINOR_SEVENTH("minorSeventh", "Малая септима", 10),
    MAJOR_SEVENTH("majorSeventh", "Большая септима", 11),
    OCTAVE("octave", "Октава", 12);

    companion object {
        fun from(value: String?): Intervals = entries.find { it.value == value } ?: MINOR_SECOND
        fun from(id: Int): Intervals = entries.find { it.ordinal == id - 1} ?: MINOR_SECOND
    }
}