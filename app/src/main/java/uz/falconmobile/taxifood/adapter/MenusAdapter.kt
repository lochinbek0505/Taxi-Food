package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.falconmobile.taxifood.databinding.FoodCategoryLayoutBinding
import uz.falconmobile.taxifood.model.category_model

class MenusAdapter(
    val context: Context,
    var items: MutableList<category_model>,
    var listener: ItemSetOnClickListener,

    ) :
    RecyclerView.Adapter<MenusAdapter.Holder>() {


    interface ItemSetOnClickListener {
        fun onClick(data: category_model)
    }


    inner class Holder(var view: FoodCategoryLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: category_model) {

            view.apply {
//                this.sekk.setOnTouchListener { _, _ ->
//                    true
//                }
                this.tvName.text = data.name
                this.ivCategory.setImageResource(data.image)

//                this.tvPr.text = "${data.percentage!!.toInt().toString()} %"
//                this.sekk.progress = data.percentage!!.toInt()
//                this.tvAuthor.text = "${data.author!!.firstName} ${data.author!!.lastName}"
//                Glide.with(context).load("https://bestedu.uz${data.author!!.image}")
//                    .into(this.tvImage)
//                this.tvName.text = data.name

            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            FoodCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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