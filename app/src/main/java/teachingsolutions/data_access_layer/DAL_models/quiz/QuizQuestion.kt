package teachingsolutions.data_access_layer.DAL_models.quiz

import java.time.LocalDateTime

data class QuizQuestion(
    val questionId: Int,
    val questionText: String,
    val updatedAt: LocalDateTime?,
    val quizQuestionType: String,
    val attachedDataSetId: Long?,
    val courseItemId: Int,
    val answers: List<QuizQuestionAnswer>
)
