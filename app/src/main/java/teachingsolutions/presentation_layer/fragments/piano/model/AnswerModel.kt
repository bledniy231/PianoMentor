package teachingsolutions.presentation_layer.fragments.piano.model

data class AnswerModel (
    val answerName: String,
    val isCorrect: Boolean,
    var firstNoteName: String? = null,
    var secondNoteName: String? = null
)