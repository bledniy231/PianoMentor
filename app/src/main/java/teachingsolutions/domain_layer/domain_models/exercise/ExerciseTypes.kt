package teachingsolutions.domain_layer.domain_models.exercise

enum class ExerciseTypes(val value: String) {
    COMPARISON_ASC("ComparisonAsc"),
    COMPARISON_DESC("ComparisonDesc"),
    DETERMINATION_ASC("DeterminationAsc"),
    DETERMINATION_DESC("DeterminationDesc"),
    COMPARISON_HARMONIOUS("ComparisonHarmonious"),
    DETERMINATION_HARMONIOUS("DeterminationHarmonious"),
    DETERMINATION_MULTIPLE("DeterminationMultiple");

    companion object {
        fun from(value: String?): ExerciseTypes = entries.find { it.value.lowercase() == value?.lowercase() } ?: COMPARISON_ASC
        fun from(id: Int): ExerciseTypes = entries.find { it.ordinal == id - 1 } ?: COMPARISON_ASC
    }
}