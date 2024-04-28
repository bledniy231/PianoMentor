package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemType
import teachingsolutions.domain_layer.mapping_models.courses.CourseModel
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemModel
import teachingsolutions.presentation_layer.fragments.courses.model.CourseImplementation
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener

class CoursesRecyclerViewAdapter(
    private val listener: ISelectRecyclerViewItemListener<CourseModel>,
    private val courseImplementation: CourseImplementation
)
    : RecyclerView.Adapter<CoursesRecyclerViewAdapter.CoursesRecyclerViewViewHolder>() {
    inner class CoursesRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title1)
        private val subtitle: TextView = itemView.findViewById(R.id.subtitle1)
        private val description: TextView = itemView.findViewById(R.id.description1)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress1)
        internal val card: CardView = itemView.findViewById(R.id.course_item_card)
        private val courseItemInfo: ConstraintLayout = itemView.findViewById(R.id.course_item_info)
        private val courseItemImageContainer: RelativeLayout = itemView.findViewById(R.id.course_item_image_container)
        private val circleBackground: ImageView = itemView.findViewById(R.id.circle_background)
        private val courseItemImage: ImageView = itemView.findViewById(R.id.course_item_image)

        internal fun bind(courseModel: CourseModel?) {
            if (courseModel == null) {
                return
            }

            if (CourseImplementation.BASE_COURSES == courseImplementation) {
                courseItemImageContainer.visibility = View.GONE
                subtitle.visibility = View.VISIBLE
                description.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                courseItemInfo.setPadding(5, 16, 16, 16)
                title.text = courseModel.title
                subtitle.text = courseModel.subtitle
                description.text = courseModel.description
                progressBar.progress = courseModel.progressInPercent
            } else {
                courseItemImageContainer.visibility = View.VISIBLE
                subtitle.visibility = View.GONE
                description.visibility = View.GONE
                progressBar.visibility = View.GONE
                val innerCourseItemModel = courseModel as CourseItemModel
                title.text = innerCourseItemModel.title
                when (innerCourseItemModel.courseItemType) {
                    CourseItemType.LECTURE -> {
                        courseItemImage.setBackgroundResource(R.drawable.icon_lectures)
                    }
                    CourseItemType.EXERCISE -> {
                        courseItemImage.setBackgroundResource(R.drawable.icon_practice)
                    }
                    else -> {
                        courseItemImage.setBackgroundResource(R.drawable.icon_lectures_tests)
                    }
                }

                when (innerCourseItemModel.courseItemProgressType) {
                    CourseItemProgressType.NOT_STARTED -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.light_gray))
                        courseItemImage.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.black))
                    }
                    CourseItemProgressType.IN_PROGRESS -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.light_blue))
                        courseItemImage.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.black))
                    }
                    CourseItemProgressType.COMPLETED -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.first_green))
                        courseItemImage.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.ultra_light_purple))
                    }
                    else -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.deep_coral))
                        courseItemImage.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.ultra_light_purple))
                    }
                }
            }
        }
    }

    private var models: List<CourseModel>? = null

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
    public fun setModelsList(list: List<CourseModel>) {
        models = list
        notifyDataSetChanged()
    }
}