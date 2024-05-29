package teachingsolutions.data_access_layer.quiz

import okhttp3.ResponseBody
import teachingsolutions.data_access_layer.DAL_models.common.DefaultResponseApi
import teachingsolutions.data_access_layer.DAL_models.quiz.GetQuizResponseApi
import teachingsolutions.data_access_layer.DAL_models.quiz.SetQuizUserAnswersRequestApi
import teachingsolutions.data_access_layer.DAL_models.quiz.SetQuizUserAnswersResponseApi
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import teachingsolutions.data_access_layer.common.ActionResult
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizDataSource @Inject constructor(private val apiService: IPianoMentorApiService) {
    suspend fun getCourseItemQuiz(courseId: Int, courseItemId: Int, userId: Long): ActionResult<GetQuizResponseApi> {
        return try {
            val result = apiService.getCourseItemQuiz(courseId, courseItemId, userId)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException(/*"Error while getting quiz of course item: ${courseItemId}, response not successful, */"${e.message}"))
        }
    }

    suspend fun setQuizUserAnswers(request: SetQuizUserAnswersRequestApi): ActionResult<SetQuizUserAnswersResponseApi> {
        return try {
            val result = apiService.setCourseItemQuizUserAnswers(request)
            when (result.errors) {
                null -> {
                    ActionResult.Success(result)
                }
                else -> {
                    ActionResult.NormalError(result)
                }
            }
        } catch (e: Exception) {
            ActionResult.ExceptionError(IOException("Error while setting quiz answers, response not successful, ${e.message}"))
        }
    }

    suspend fun getQuizQuestionFile(dataSetId: Long): ActionResult<ResponseBody> {
        val response = apiService.getQuizQuestionFile(dataSetId)
        return if (response.isSuccessful) {
            ActionResult.Success(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string() ?: response.body()?.string()
            ActionResult.ExceptionError(IOException("$errorBody"))
        }
    }
}