package teachingsolutions.presentation_layer.fragments.quiz.model

import java.io.File

data class QuestionViewPagerUI(
    val questionId: Int,
    val questionText: String,
    val quizQuestionType: String,
    val attachedFile: File? = null,
    val courseItemId: Int,
    val answers: List<QuestionAnswerUI>
)
