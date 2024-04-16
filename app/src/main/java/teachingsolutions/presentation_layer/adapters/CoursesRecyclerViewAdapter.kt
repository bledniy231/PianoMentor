package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.data.courses.model.CourseRecyclerViewItemModel
import teachingsolutions.presentation_layer.fragments.data.main_menu.model.MainMenuRecyclerViewItemModel
import teachingsolutions.presentation_layer.fragments.ui.courses.CoursesFragment
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener
import java.lang.Math.ceil

class CoursesRecyclerViewAdapter(private var listener: ISelectRecyclerViewItemListener<CourseRecyclerViewItemModel>)
    : RecyclerView.Adapter<CoursesRecyclerViewAdapter.CoursesRecyclerViewViewHolder>() {
    inner class CoursesRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title1)
        private val subtitle: TextView = itemView.findViewById(R.id.subtitle1)
        private val description: TextView = itemView.findViewById(R.id.description1)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress1)
        internal val card: CardView = itemView.findViewById(R.id.course_item_card)

        internal fun bind(courseModel: CourseRecyclerViewItemModel?) {
            if (courseModel == null) {
                return
            }

            title.text = courseModel.title
            subtitle.text = courseModel.subtitle
            description.text = courseModel.description
            progressBar.progress = courseModel.progressInPercent
        }
    }

    private var models: List<CourseRecyclerViewItemModel>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoursesRecyclerViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return CoursesRecyclerViewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models?.size ?: 0
    }

    override fun onBindViewHolder(holder: CoursesRecyclerViewViewHolder, position: Int) {
        holder.bind(models?.get(position))
        holder.card.setOnClickListener {
            models?.get(position)?.let { it1 -> listener.onItemSelected(it1) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<CourseRecyclerViewItemModel>) {
        models = list
        notifyDataSetChanged()
    }
}