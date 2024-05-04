package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsViewPagerItemModelUI

class StatisticsViewPagerAdapter(
    fragmentContext: Context) :
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

            if (position % 2 == 0) {
                progressBarView.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_green, resources.newTheme())
            } else {
                progressBarView.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_brown, resources.newTheme())
            }
            progressBarView.progress = statModel.progressValueInPercent
            progressCounter.text = statModel.progressValueAbsolute.toString()
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
        return models?.size ?: 0
    }

    override fun onBindViewHolder(holder: StatisticsViewPagerViewHolder, position: Int) {
        holder.bind(models?.get(position), position, fragmentContextInstance.resources)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setModelsList(list: List<StatisticsViewPagerItemModelUI>) {
        models = list
        notifyDataSetChanged()
    }
}