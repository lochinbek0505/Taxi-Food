package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.falconmobile.taxifood.databinding.RestouranLayoutBinding
import uz.falconmobile.taxifood.model.restouran_model

class RestouranAdapter(
    val context: Context,
    var items: List<restouran_model>?,
    var listener: ItemSetOnClickListener,

    ) :
    RecyclerView.Adapter<RestouranAdapter.Holder>() {


    interface ItemSetOnClickListener {
        fun onClick(data: restouran_model)
    }


    inner class Holder(var view: RestouranLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: restouran_model) {

            view.apply {
//                this.sekk.setOnTouchListener { _, _ ->
//                    true
//                }
                this.tvName.text = data.name
//                this.ivRestouran.setImageResource(data.banner)
                this.tvLocate.text = data.location
                this.tvLenght.text = data.lenght
//                this.tvPr.text = "${data.percentage!!.toInt().toString()} %"
//                this.sekk.progress = data.percentage!!.toInt()
//                this.tvAuthor.text = "${data.author!!.firstName} ${data.author!!.lastName}"
                Glide.with(context).load(data.banner)
                    .into(this.ivRestouran)
//                this.tvName.text = data.name

            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            RestouranLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(
            binding
        )


    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items!![position]

        holder.bind(item)



        holder.itemView.setOnClickListener {


            listener.onClick(item)


        }
    }


    override fun getItemCount(): Int = items?.count()!!

}