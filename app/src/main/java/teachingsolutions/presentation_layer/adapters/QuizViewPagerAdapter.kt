package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.content.Context
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

            questionCounter.text = String.format(context.getString(R.string.question_counter), position + 1, _models?.size)
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
                    text = answer.answerText
                    isChecked = answer.wasChosenByUser ?: false
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                        setMargins(0, 3, 0, 3)
                    }
                    setPadding(10, 0, 10, 0)
                    buttonDrawable = AppCompatResources.getDrawable(context, R.drawable.btn_radio_padding)
                    background = AppCompatResources.getDrawable(context, R.drawable.btn_radio_background)
                }
                radioGroup.addView(radioButton)
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
                question.answers.find { it.answerText == checkedRadioButton.text.toString() }?.wasChosenByUser = checkedRadioButton.isChecked
            }
        }
    }

    private var _models: List<QuestionViewPagerUI>? = null
    val models: List<QuestionViewPagerUI> = _models!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_card, parent, false)
        return QuizViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _models?.size ?: 0
    }

    override fun onBindViewHolder(holder: QuizViewPagerViewHolder, position: Int) {
        holder.bind(_models?.get(position), position, fragmentContext)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<QuestionViewPagerUI>) {
        _models = list
        notifyDataSetChanged()
    }
}