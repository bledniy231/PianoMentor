package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pianomentor.R
import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionAnswerUI
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionViewPagerUI

class QuizViewPagerAdapter(private val fragmentContext: Context): RecyclerView.Adapter<QuizViewPagerAdapter.QuizViewPagerViewHolder>() {

    inner class QuizViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val questionTitle: TextView = itemView.findViewById(R.id.questionTitle)
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val image: ImageView = itemView.findViewById(R.id.questionImage)
        private val radioGroup: RadioGroup = itemView.findViewById(R.id.answerGroup)
        private val questionCounter: TextView = itemView.findViewById(R.id.quizItemCounter)
        internal fun bind(question: QuestionViewPagerUI?, position: Int, context: Context) {
            if (question == null) {
                return
            }

            questionCounter.text = String.format(context.getString(R.string.question_counter), position + 1, models?.size)
            questionTitle.text = String.format(context.getString(R.string.question_title), position + 1)
            questionText.text = question.questionText
            if (question.attachedFile != null) {
                image.visibility = View.VISIBLE
                Glide.with(fragmentContext).load(question.attachedFile).into(image)
            } else {
                image.visibility = View.GONE
            }

            radioGroup.removeAllViews()
            for (answer in question.answers) {
                val radioButton = RadioButton(fragmentContext).apply {
                    id = answer.answerId
                    //setTag(R.id.tag_additional_text, question.questionId)
                    text = answer.answerText
                    textSize = 16f
                    isChecked = answer.wasChosenByUser ?: false
                    isEnabled = _startQuiz
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                        setMargins(0, 3, 0, 3)
                    }
                    setPadding(10, 0, 10, 0)
                    buttonDrawable = AppCompatResources.getDrawable(context, R.drawable.btn_radio_padding)
                    background = if (_startQuiz) {
                        AppCompatResources.getDrawable(context, R.drawable.btn_radio_background_correct)
                    } else if (!answer.isCorrect) {
                        AppCompatResources.getDrawable(context, R.drawable.btn_radio_background_incorrect)
                    } else {
                        AppCompatResources.getDrawable(context, R.drawable.btn_radio_background_correct)
                    }
                }
                radioButton.setAutoSizeTextTypeUniformWithConfiguration(
                    12,
                    24,
                    2,
                    TypedValue.COMPLEX_UNIT_SP
                )
                radioGroup.addView(radioButton)
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
                val userAnswer = answers?.find { it.answerId == checkedRadioButton.id }
                userAnswer?.wasChosenByUser = checkedRadioButton.isChecked

                for (i in 0 until group.childCount) {
                    val radioButton = group.getChildAt(i) as? RadioButton
                    if (radioButton != null && radioButton.id != checkedId) {
                        val answer = answers?.find { it.answerId == radioButton.id }
                        answer?.wasChosenByUser = !checkedRadioButton.isChecked
                    }
                }
            }
        }
    }

    var models: List<QuestionViewPagerUI>? = null

    private var _startQuiz: Boolean = false
    private var _quizStatus: CourseItemProgressType? = null
    private var answers: List<QuestionAnswerUI>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_card, parent, false)
        return QuizViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models?.size ?: 0
    }

    override fun onBindViewHolder(holder: QuizViewPagerViewHolder, position: Int) {
        holder.bind(models?.get(position), position, fragmentContext)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<QuestionViewPagerUI>, startQuiz: Boolean, quizStatus: CourseItemProgressType) {
        if (startQuiz && quizStatus == CourseItemProgressType.FAILED) {
            for (question in list) {
                for (answer in question.answers) {
                    answer.wasChosenByUser = false
                }
            }
        }
        models = list
        answers = models?.flatMap { it.answers }
        _startQuiz = startQuiz
        _quizStatus = quizStatus
        notifyDataSetChanged()
    }
}