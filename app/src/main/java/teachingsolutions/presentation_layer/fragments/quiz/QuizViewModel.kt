package teachingsolutions.presentation_layer.fragments.quiz

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pianomentor.R
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
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

    private val _quizQuestions = MutableLiveData<GetQuizResponseUI?>()
    val quizQuestions: LiveData<GetQuizResponseUI?> = _quizQuestions

    private val _quizSavingResult = MutableLiveData<DefaultResponseUI?>()
    val quizSavingResult: LiveData<DefaultResponseUI?> = _quizSavingResult

    private val _alertDialogResult = MutableLiveData<Boolean?>()
    val alertDialogResult: LiveData<Boolean?> = _alertDialogResult

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

    fun showAlertDialog(quizStatus: CourseItemProgressType, context: Context, quizCompleteButton: MaterialButton) {
        when (quizStatus) {
            CourseItemProgressType.FAILED -> {
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.quiz_failed))
                    .setMessage(context.getString(R.string.restart_quiz_or_show_results))
                    .setPositiveButton(context.getString(R.string.restart_quiz)) { _, _ ->
                        quizCompleteButton.visibility = View.VISIBLE
                        _alertDialogResult.postValue(true)
                    }
                    .setNegativeButton(context.getString(R.string.show_results)) { _, _ ->
                        quizCompleteButton.visibility = View.GONE
                        _alertDialogResult.postValue(false)
                    }
                    .setOnCancelListener {
                        quizCompleteButton.visibility = View.GONE
                        _alertDialogResult.postValue(false)
                    }
                    .show()
            }
            CourseItemProgressType.COMPLETED -> {
                quizCompleteButton.visibility = View.GONE
                _alertDialogResult.postValue(false)
            }
            else -> {
                _alertDialogResult.postValue(true)
            }
        }
    }

    fun calculateQuizResults(questions: List<QuestionViewPagerUI>): Triple<Int, Int, Double> {
        var correctUserAnswers = 0
        var correctDefaultAnswers = 0
        for (question in questions) {
            for (answer in question.answers) {
                if (answer.isCorrect && answer.wasChosenByUser == true) {
                    correctUserAnswers++
                }
                if (answer.isCorrect) {
                    correctDefaultAnswers++
                }
            }
        }

        val correctAnswersPercentage = if (correctDefaultAnswers > 0) correctUserAnswers.toDouble() / correctDefaultAnswers * 100 else 0.0
        return Triple(correctDefaultAnswers, correctUserAnswers, correctAnswersPercentage)
    }
}