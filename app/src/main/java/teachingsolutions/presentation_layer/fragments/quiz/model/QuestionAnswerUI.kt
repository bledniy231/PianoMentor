package teachingsolutions.presentation_layer.fragments.quiz.model

data class QuestionAnswerUI(
    val answerId: Int,
    val answerText: String,
    val isCorrect: Boolean,
    var wasChosenByUser: Boolean? = null,
    val userAnswerText: String? = null
)
