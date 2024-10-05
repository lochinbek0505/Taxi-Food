package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.falconmobile.taxifood.databinding.MenuItemLayoutBinding
import uz.falconmobile.taxifood.model.menu_model

class MenuRestouranAdapter(
    val context: Context,
    var items: MutableList<menu_model>,
    var listener: ItemSetOnClickListener,

    ) :
    RecyclerView.Adapter<MenuRestouranAdapter.Holder>() {


    interface ItemSetOnClickListener {
        fun onClick(data: menu_model)
    }


    inner class Holder(var view: MenuItemLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: menu_model) {

            view.apply {
//                this.sekk.setOnTouchListener { _, _ ->
//                    true
//                }
                this.tvName.text = data.name
//                this.ivCategory.setImageResource(data.image)

                this.tvCount.text = data.count.toString()
//                this.tvPr.text = "${data.percentage!!.toInt().toString()} %"
//                this.sekk.progress = data.percentage!!.toInt()
//                this.tvAuthor.text = "${data.author!!.firstName} ${data.author!!.lastName}"
//                Glide.with(context).load(data.banner)
//                    .into(this.ivCategory)
//                this.tvName.text = data.name

            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            MenuItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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