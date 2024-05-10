package teachingsolutions.data_access_layer.DAL_models.quiz

data class QuizQuestionAnswer(
    val answerId: Int,
    val answerText: String,
    val isCorrect: Boolean,
    val wasChosenByUser: Boolean? = null,
    val userAnswerText: String? = null
)
