package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.falconmobile.taxifood.databinding.RestouranLayoutBinding
import uz.falconmobile.taxifood.model.restouran_model
import java.util.*

class RestouranAdapter(
    val context: Context,
    var items: List<restouran_model>?,
    var listener: ItemSetOnClickListener,
) : RecyclerView.Adapter<RestouranAdapter.Holder>() {

    interface ItemSetOnClickListener {
        fun onClick(data: restouran_model, position: Int)
    }

    inner class Holder(var view: RestouranLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(data: restouran_model) {
            view.apply {
                this.tvName.text = data.name
                this.tvLocate.text = data.location
                this.tvLenght.text = "${data.distance} km"
                this.tvRate.text="${data.rate}"
                // Prepare banner images for ViewPager2
                val banners = listOf(data.banner1, data.banner2, data.banner3)
                val imagePagerAdapter = ImagePagerAdapter(context, banners)
                this.viewPager.adapter = imagePagerAdapter

                // Auto slide logic for carousel
                val handler = Handler()
                var currentPage = 0
                val update = Runnable {
                    if (currentPage == banners.size) {
                        currentPage = 0
                    }
                    this.viewPager.setCurrentItem(currentPage++, true)
                }

                // Set timer for auto image slide (every 2 seconds)
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 3000, 3000) // Delay of 2 seconds
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RestouranLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items!![position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener.onClick(item, position)
        }
    }

    override fun getItemCount(): Int = items?.count() ?: 0
}
