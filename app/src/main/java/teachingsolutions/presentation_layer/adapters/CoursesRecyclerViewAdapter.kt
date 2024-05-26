package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemType
import teachingsolutions.presentation_layer.fragments.courses.model.CourseImplementation
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseModelUI
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener

class CoursesRecyclerViewAdapter(
    private val listener: ISelectRecyclerViewItemListener<CourseModelUI>,
    private val courseImplementation: CourseImplementation)
    : RecyclerView.Adapter<CoursesRecyclerViewAdapter.CoursesRecyclerViewViewHolder>() {
    inner class CoursesRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title1)
        private val subtitle: TextView = itemView.findViewById(R.id.subtitle1)
        private val description: TextView = itemView.findViewById(R.id.description1)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress1)
        internal val card: CardView = itemView.findViewById(R.id.course_item_card)
        private val courseItemInfo: LinearLayout = itemView.findViewById(R.id.course_item_info)
        private val courseItemImageContainer: FrameLayout = itemView.findViewById(R.id.course_item_image_container)
        private val circleBackground: ImageView = itemView.findViewById(R.id.circle_background)
        private val courseItemImage: ImageView = itemView.findViewById(R.id.course_item_image)

        internal fun bind(courseModelUI: CourseModelUI?) {
            if (courseModelUI == null) {
                return
            }

            if (CourseImplementation.BASE_COURSES == courseImplementation) {
                courseItemImageContainer.visibility = View.GONE
                subtitle.visibility = View.VISIBLE
                description.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                courseItemInfo.setPadding(32)
                title.text = courseModelUI.title
                subtitle.text = courseModelUI.subtitle
                description.text = courseModelUI.description
                progressBar.progress = courseModelUI.progressInPercent
            } else {
                courseItemImageContainer.visibility = View.VISIBLE
                title.visibility = View.VISIBLE
                subtitle.visibility = View.GONE
                description.visibility = View.GONE
                progressBar.visibility = View.GONE
                val courseItemModelUI = courseModelUI as CourseItemModelUI
                courseItemInfo.setPadding(16, 48, 32, 48)
                title.text = courseItemModelUI.title
                title.textSize = 18f
                when (courseItemModelUI.courseItemType) {
                    CourseItemType.LECTURE -> {
                        courseItemImage.setImageResource(R.drawable.icon_lectures)
                    }
                    CourseItemType.EXERCISE -> {
                        courseItemImage.setImageResource(R.drawable.icon_practice)
                    }
                    else -> {
                        courseItemImage.setImageResource(R.drawable.icon_tests)
                    }
                }

                when (courseItemModelUI.courseItemProgressType) {
                    CourseItemProgressType.NOT_STARTED -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.bright_purple))
                        courseItemImage.setColorFilter(ContextCompat.getColor(itemView.context, R.color.dark_gray))
                    }
                    CourseItemProgressType.IN_PROGRESS -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.bright_blue_for_course_icon))
                        courseItemImage.setColorFilter(ContextCompat.getColor(itemView.context, R.color.ultra_light_purple))
                    }
                    CourseItemProgressType.COMPLETED -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.navy_green))
                        courseItemImage.setColorFilter(ContextCompat.getColor(itemView.context, R.color.ultra_light_purple))
                    }
                    else -> {
                        circleBackground.setColorFilter(ContextCompat.getColor(itemView.context, R.color.deep_coral))
                        courseItemImage.setColorFilter(ContextCompat.getColor(itemView.context, R.color.ultra_light_purple))
                    }
                }
            }
        }
    }

    private var models: List<CourseModelUI>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoursesRecyclerViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_card, parent, false)
        return CoursesRecyclerViewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models?.size ?: 0
    }

    override fun onBindViewHolder(holder: CoursesRecyclerViewViewHolder, position: Int) {
        holder.bind(models?.get(position))
        holder.card.setOnClickListener {
            models?.get(position)?.let { listener.onItemSelected(it) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<CourseModelUI>) {
        models = list
        notifyDataSetChanged()
    }
}