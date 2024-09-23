package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.falconmobile.taxifood.databinding.FavoriteRestouranBinding
import uz.falconmobile.taxifood.db.models.FavoriteRestaurants

class FavoriteRestouranAdapter(
    val context: Context,
    var items: MutableList<FavoriteRestaurants>?,
    var listener: ItemSetOnClickListener,

    ) :
    RecyclerView.Adapter<FavoriteRestouranAdapter.Holder>() {


    interface ItemSetOnClickListener {
        fun onClick(data: FavoriteRestaurants, position: Int)
    }


    inner class Holder(var view: FavoriteRestouranBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: FavoriteRestaurants) {

            view.apply {
//                this.sekk.setOnTouchListener { _, _ ->
//                    true
//                }
                this.tvName.text = data.name
//                this.ivRestouran.setImageResource(data.banner)
                this.tvLocate.text = data.locate
                this.tvLenght.text = "${data.distance} km"
//                this.tvPr.text = "${data.percentage!!.toInt().toString()} %"
//                this.sekk.progress = data.percentage!!.toInt()
//                this.tvAuthor.text = "${data.author!!.firstName} ${data.author!!.lastName}"
                Glide.with(context).load(data.image)
                    .into(this.ivRestouran)
//                this.tvName.text = data.name

            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            FavoriteRestouranBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(
            binding
        )


    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items!![position]

        holder.bind(item)



        holder.itemView.setOnClickListener {


            listener.onClick(item, position)


        }
    }

    fun deleteItem(position: Int) {
        items!!.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun getItemCount(): Int = items?.count()!!

}