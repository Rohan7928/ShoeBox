package com.example.shoebox.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoebox.ui.CheckoutItem
import com.shoebox.R
import com.example.shoebox.model.DataModel

class ItemsListAdapter(
    var context: Context,
    var itemModel: ArrayList<DataModel>,
    var fromWhere: String
) : RecyclerView.Adapter<ItemsListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (fromWhere === "ChekoutItem") {
            LayoutInflater.from(context).inflate(R.layout.you_like_items_card, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.items_card, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = itemModel[position].name
        holder.txtPrice.text = itemModel[position].price
        holder.txtDesc.text = itemModel[position].description
        Glide.with(context)
            .load(itemModel[position].image)
            .placeholder(R.mipmap.one)
            .into(holder.imageView)
        holder.itemCard.setOnClickListener {
            val intent = Intent(context, CheckoutItem::class.java)
            intent.putExtra("shoeId",itemModel[position].id)
            intent.putExtra("itemName",itemModel[position].name)
            intent.putExtra("itemPrice",itemModel[position].price)
            intent.putExtra("itemDesc",itemModel[position].description)
            intent.putExtra("itemImage",itemModel[position].image)
            intent.putExtra("itemList",itemModel)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return itemModel.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var txtName: TextView
        var txtPrice: TextView
        var txtDesc: TextView
        var itemCard: CardView

        init {
            imageView = itemView.findViewById(R.id.imageView)
            txtName = itemView.findViewById(R.id.textViewItem)
            txtPrice = itemView.findViewById(R.id.textViewPrice)
            txtPrice = itemView.findViewById(R.id.textViewPrice)
            txtDesc = itemView.findViewById(R.id.textViewDesc)
            itemCard = itemView.findViewById(R.id.card_item)
        }
    }
}