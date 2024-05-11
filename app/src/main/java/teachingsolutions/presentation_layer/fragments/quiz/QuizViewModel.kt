package teachingsolutions.presentation_layer.fragments.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.quiz.QuizRepository
import teachingsolutions.domain_layer.user.UserRepository
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.quiz.model.GetQuizResponseUI
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionViewPagerUI
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository): ViewModel() {

    private val _quizQuestions = MutableLiveData<GetQuizResponseUI>()
    val quizQuestions: LiveData<GetQuizResponseUI> = _quizQuestions

    private val _quizSavingResult = MutableLiveData<DefaultResponseUI>()
    val quizSavingResult: LiveData<DefaultResponseUI> = _quizSavingResult

    fun getQuizQuestions(courseId: Int, courseItemId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = quizRepository.getQuizQuestions(courseId, courseItemId, userRepository.userId ?: throw IllegalStateException("User id is null"))
                _quizQuestions.postValue(result)
            }
        }
    }

    fun setQuizResult(courseId: Int, courseItemId: Int, isQuizCompletedByUser: Boolean, resultModels: List<QuestionViewPagerUI>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = quizRepository.setQuizResult(courseId, courseItemId, isQuizCompletedByUser, resultModels)
                _quizSavingResult.postValue(result)
            }
        }
    }

    fun calculateQuizResults(questions: List<QuestionViewPagerUI>): Triple<Int, Int, Double> {
        var correctUserAnswers = 0
        var correctAnswers = 0
        for (question in questions) {
            for (answer in question.answers) {
                if (answer.isCorrect && answer.wasChosenByUser == true) {
                    correctUserAnswers++
                }
                if (answer.isCorrect) {
                    correctAnswers++
                }
            }
        }

        val correctAnswersPercentage = if (correctAnswers > 0) correctUserAnswers.toDouble() / correctAnswers * 100 else 0.0
        return Triple(correctAnswers, correctUserAnswers, correctAnswersPercentage)
    }
}