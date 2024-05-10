package teachingsolutions.domain_layer.quiz

import teachingsolutions.data_access_layer.DAL_models.quiz.QuizQuestion
import teachingsolutions.data_access_layer.DAL_models.quiz.QuizQuestionAnswer
import teachingsolutions.data_access_layer.DAL_models.quiz.SetQuizUserAnswersRequestApi
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.courses.CoursesDataSource
import teachingsolutions.data_access_layer.quiz.QuizDataSource
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.quiz.model.GetQuizResponseUI
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionAnswerUI
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionViewPagerUI
import java.io.File
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val quizDataSource: QuizDataSource,
    private val userRepository: UserRepository) {

    suspend fun getQuizQuestions(courseId: Int, courseItemId: Int, userId: Long): GetQuizResponseUI {
        return when (val result = quizDataSource.getCourseItemQuiz(courseId, courseItemId, userId)) {
            is ActionResult.Success -> {
                GetQuizResponseUI(
                    result.data.questionModels?.map {
                        QuestionViewPagerUI(
                            it.questionId,
                            it.questionText,
                            it.quizQuestionType,
                            getQuestionFile(it.attachedDataSetId),
                            it.courseItemId,
                            it.answers.map { answer ->
                                QuestionAnswerUI(
                                    answer.answerId,
                                    answer.answerText,
                                    answer.isCorrect
                                )
                            }
                        )
                    }
                )
            }
            is ActionResult.NormalError -> {
                GetQuizResponseUI(null, result.data.errors?.joinToString { it } ?: "Error while getting quiz questions")
            }
            is ActionResult.ExceptionError -> {
                GetQuizResponseUI(null, result.exception.message ?: "Error while getting quiz questions")
            }
        }
    }

    suspend fun setQuizResult(courseId: Int, courseItemId: Int, isQuizCompletedByUser: Boolean, resultModels: List<QuestionViewPagerUI>): DefaultResponseUI {
        val request = SetQuizUserAnswersRequestApi(
            userRepository.userId ?: throw IllegalStateException("User id is null"),
            courseId,
            courseItemId,
            isQuizCompletedByUser,
            resultModels.map {
                QuizQuestion(
                    it.questionId,
                    it.questionText,
                    null,
                    it.quizQuestionType,
                    null,
                    it.courseItemId,
                    it.answers.map { answer ->
                        QuizQuestionAnswer(
                            answer.answerId,
                            answer.answerText,
                            answer.isCorrect
                        )
                    }
                )
            }
        )

        return when (val result = quizDataSource.setQuizUserAnswers(request)) {
            is ActionResult.Success -> DefaultResponseUI(null)
            is ActionResult.NormalError -> DefaultResponseUI(result.data._errors?.joinToString { it } ?: "Error while setting quiz answers")
            is ActionResult.ExceptionError -> DefaultResponseUI(result.exception.message ?: "Error while setting quiz answers")
        }
    }

    private suspend fun getQuestionFile(dataSetId: Long?): File? {
        if (dataSetId == null) return null

        return when (val result = quizDataSource.getQuizQuestionFile(dataSetId)) {
            is ActionResult.Success -> {
                val file = File.createTempFile("quiz_image_$dataSetId", null)
                result.data.byteStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file
            }
            else -> null
        }
    }
}