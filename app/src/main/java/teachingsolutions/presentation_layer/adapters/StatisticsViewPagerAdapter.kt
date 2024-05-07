package teachingsolutions.presentation_layer.adapters

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsViewPagerItemModelUI

class StatisticsViewPagerAdapter(
    private val fragmentContext: Context) :
    RecyclerView.Adapter<StatisticsViewPagerAdapter.StatisticsViewPagerViewHolder>() {

    inner class StatisticsViewPagerViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        private val progressBarView: ProgressBar = itemView.findViewById(R.id.cardview_progress_bar)
        private val progressCounter: TextView = itemView.findViewById(R.id.cardview_counter_text)
        private val title: TextView = itemView.findViewById(R.id.cardview_title_text)
        private val description: TextView = itemView.findViewById(R.id.cardview_description_text)

        internal fun bind(
            statModel: StatisticsViewPagerItemModelUI?,
            position: Int,
            resources: Resources) {
            if (statModel == null) {
                return
            }

            when (position % 3) {
                0 -> progressBarView.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_green, resources.newTheme())
                1 -> progressBarView.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_brown, resources.newTheme())
                2 -> progressBarView.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_blue, resources.newTheme())
            }
            val progressBarAnim = ObjectAnimator.ofInt(progressBarView, "progress", 0, statModel.progressValueInPercent)
            progressBarAnim.duration = 500
            progressBarAnim.interpolator = DecelerateInterpolator()

            val textValueAnim = ValueAnimator.ofInt(0, statModel.progressValueAbsolute)
            textValueAnim.duration = 500
            textValueAnim.addUpdateListener { anim ->
                progressCounter.text = anim.animatedValue.toString()
            }

            progressBarAnim.start()
            textValueAnim.start()

            title.text = statModel.titleText
            description.text = statModel.descriptionText
        }
    }

    private var models: List<StatisticsViewPagerItemModelUI>? = null
    private val fragmentContextInstance: Context = fragmentContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewPagerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.statistics_viewpager_item, parent, false)
        return StatisticsViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    override fun onBindViewHolder(holder: StatisticsViewPagerViewHolder, position: Int) {
        val realPosition = position % models?.size!!
        holder.bind(models?.get(realPosition), realPosition, fragmentContext.resources)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<StatisticsViewPagerItemModelUI>) {
        models = list
        notifyDataSetChanged()
    }
}