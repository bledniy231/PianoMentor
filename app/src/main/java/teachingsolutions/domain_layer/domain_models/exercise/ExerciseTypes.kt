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
        fun from(type: String?): ExerciseTypes = entries.find { it.value.lowercase() == type?.lowercase() } ?: COMPARISON_ASC
    }
}