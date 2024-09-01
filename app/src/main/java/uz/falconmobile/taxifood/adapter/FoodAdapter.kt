package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.falconmobile.taxifood.databinding.FoodLayoutBinding
import uz.falconmobile.taxifood.model.food_model

class FoodAdapter(
    val context: Context,
    var items: MutableList<food_model>,
    var listener: ItemSetOnClickListener,

    ) :
    RecyclerView.Adapter<FoodAdapter.Holder>() {


    interface ItemSetOnClickListener {
        fun onClick(data: food_model)
    }


    inner class Holder(var view: FoodLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: food_model) {

            view.apply {


//                this.sekk.setOnTouchListener { _, _ ->
//                    true
//                }
                this.tvName.text = data.foodName
                this.tvPrice.text = data.star
                this.tvDescription.text = data.description
                this.tvCountStar.text = data.star_count
                this.tvStar.text = data.star
                Glide.with(context).load(data.image)
                    .into(this.ivFood)
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            FoodLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(
            binding
        )


    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]

        holder.bind(item)



        holder.itemView.setOnClickListener {


            listener.onClick(item)


        }
    }


    override fun getItemCount(): Int = items.count()

}