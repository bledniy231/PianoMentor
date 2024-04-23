package teachingsolutions.presentation_layer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import teachingsolutions.domain_layer.mapping_models.MainMenuRecyclerViewItemModel
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener

class MainMenuRecyclerViewAdapter(private var listener: ISelectRecyclerViewItemListener<MainMenuRecyclerViewItemModel>)
     : RecyclerView.Adapter<MainMenuRecyclerViewAdapter.MainMenuRecyclerViewViewHolder>() {
     inner class MainMenuRecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
          private val icon: ImageView = itemView.findViewById(R.id.main_menu_recycler_view_icon)
          private val title: TextView = itemView.findViewById(R.id.main_menu_recycler_view_title_text)
          internal val card: CardView = itemView.findViewById(R.id.main_menu_recycler_view_item)

          internal fun bind(mainMenuModel: MainMenuRecyclerViewItemModel?) {
               if (mainMenuModel == null) {
                    return
               }

               icon.setImageResource(mainMenuModel.iconId)
               title.text = mainMenuModel.titleText
          }
     }

     private var models: List<MainMenuRecyclerViewItemModel>? = null

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuRecyclerViewViewHolder {
          val view = LayoutInflater.from(parent.context).inflate(R.layout.main_menu_recycler_view_item, parent, false)
          return MainMenuRecyclerViewViewHolder(view)
     }

     override fun getItemCount(): Int {
          return models?.size ?: 0
     }

     override fun onBindViewHolder(holder: MainMenuRecyclerViewViewHolder, position: Int) {
          holder.bind(models?.get(position))
          holder.card.setOnClickListener {
                models?.get(position)?.let { it1 -> listener.onItemSelected(it1) }
          }
     }

     @SuppressLint("NotifyDataSetChanged")
     public fun setModelsList(list: List<MainMenuRecyclerViewItemModel>) {
          models = list
          notifyDataSetChanged()
     }
}