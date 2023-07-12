package com.example.shoebox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoebox.interfaces.OnCartItemClick
import com.example.shoebox.model.DataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoebox.R

class CartListAdapter(
    var context: Context?,
    var itemModel: ArrayList<DataModel>?,
    var fromWhere: String,
    var db: FirebaseFirestore,
    var onCartItemClick: OnCartItemClick
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.checkout_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = itemModel?.get(position)?.name
        holder.txtPrice.text = itemModel?.get(position)?.price
        holder.txtCount.text = itemModel?.get(position)?.count.toString()
        if (fromWhere === "ItemsListFrag") {
            holder.imageAddItem.visibility = View.GONE
            holder.imageDeleteItem.visibility = View.GONE
            holder.txtCount.visibility = View.GONE
            holder.imageDeleteAdminItem.visibility = View.VISIBLE
        }
        holder.imageAddItem.setOnClickListener { view: View? ->
            var count = holder.txtCount.text.toString().toInt()
            count++
            holder.txtCount.text = count.toString()
            val dataModel = DataModel(
                itemModel?.get(position)?.name,
                itemModel?.get(position)?.price,
                itemModel?.get(position)?.description,
                itemModel?.get(position)?.image,
                itemModel?.get(position)?.id,
                count
            )
            db.collection("shoeList").document("CheckoutList")
                .collection(FirebaseAuth.getInstance().uid.toString()).document(
                    itemModel?.get(position)?.name.toString()
            ).set(dataModel).addOnSuccessListener { unused: Void? ->
                //notifyItemChanged(position);
                //notifyItemRangeChanged(position, getItemCount());
                Toast.makeText(context, "Item Updated Successfully", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e: Exception? ->
                    Toast.makeText(
                        context,
                        "Sorry, Unable to process",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            onCartItemClick.addItemValue(itemModel?.get(position))
        }
        holder.imageDeleteItem.setOnClickListener { view: View? ->
            var count = holder.txtCount.text.toString().toInt()
            count--
            val dataModel = DataModel(
                itemModel?.get(position)?.name,
                itemModel?.get(position)?.price,
                itemModel?.get(position)?.description,
                itemModel?.get(position)?.image,
                itemModel?.get(position)?.id,
                count
            )
            if (count == 0) {
                db.collection("shoeList").document("CheckoutList")
                    .collection(FirebaseAuth.getInstance().uid.toString()).document(
                    itemModel?.get(position)?.name.toString()
                ).delete().addOnSuccessListener { unused: Void? ->
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener { e: Exception? ->
                        Toast.makeText(
                            context, "Sorry, Unable to process", Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                db.collection("shoeList").document("CheckoutList")
                    .collection(FirebaseAuth.getInstance().uid.toString()).document(
                    itemModel?.get(position)?.name.toString()
                ).set(dataModel).addOnSuccessListener { unused: Void? ->
                    //notifyItemChanged(position);
                    //notifyItemRangeChanged(position, getItemCount());
                    Toast.makeText(context, "Item Updated Successfully", Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener { e: Exception? ->
                        Toast.makeText(
                            context, "Sorry, Unable to process", Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            holder.txtCount.text = count.toString()
            onCartItemClick.deleteItemValue(dataModel)
        }
        holder.imageDeleteAdminItem.setOnClickListener { view: View? ->
            val dataModel = DataModel(
                itemModel?.get(position)?.name,
                itemModel?.get(position)?.price,
                itemModel?.get(position)?.description,
                itemModel?.get(position)?.image,
                itemModel?.get(position)?.id,
                itemModel?.get(position)?.count
            )
            db.collection("shoeList").document("category").collection("men")
                .document(itemModel?.get(position)?.name.toString()).delete().addOnSuccessListener { unused: Void? ->
                notifyDataSetChanged()
                itemModel?.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
                onCartItemClick.deleteItemValue(dataModel)
                Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e: Exception? ->
                    Toast.makeText(
                        context,
                        "Sorry, Unable to process",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        context?.let {
            Glide.with(it)
                .load(itemModel?.get(position)?.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return itemModel?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var imageAddItem: ImageView
        var imageDeleteItem: ImageView
        var imageDeleteAdminItem: ImageView
        var txtName: TextView
        var txtPrice: TextView
        var txtCount: TextView
        //var itemCard: CardView

        init {
            imageView = itemView.findViewById(R.id.img_item)
            imageAddItem = itemView.findViewById(R.id.img_add)
            imageDeleteItem = itemView.findViewById(R.id.img_delete)
            imageDeleteAdminItem = itemView.findViewById(R.id.img_delete_admin)
            txtName = itemView.findViewById(R.id.txt_name_item)
            txtPrice = itemView.findViewById(R.id.txt_price_item)
            txtCount = itemView.findViewById(R.id.txt_count)
            //itemCard = itemView.findViewById(R.id.card_item)
        }
    }
}