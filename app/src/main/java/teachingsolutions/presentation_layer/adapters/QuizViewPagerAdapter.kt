package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.quiz.model.QuizViewPagerItemModelUI

class QuizViewPagerAdapter: RecyclerView.Adapter<QuizViewPagerAdapter.QuizViewPagerViewHolder>() {

    inner class QuizViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        internal fun bind(item: QuizViewPagerItemModelUI, position: Int) {

        }
    }

    private var models: List<QuizViewPagerItemModelUI>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_card, parent, false)
        return QuizViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models?.size ?: 0
    }

    override fun onBindViewHolder(holder: QuizViewPagerViewHolder, position: Int) {
        holder.bind(models!![position], position)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<QuizViewPagerItemModelUI>) {
        models = list
        notifyDataSetChanged()
    }
}