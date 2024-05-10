package teachingsolutions.data_access_layer.DAL_models.quiz

data class SetQuizUserAnswersRequestApi(
    val userId: Long,
    val courseId: Int,
    val courseItemId: Int,
    val isQuizCompletedByUser: Boolean,
    val questionsWithUserAnswers: List<QuizQuestion>
)