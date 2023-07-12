package com.example.shoebox.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.shoebox.R
import com.example.shoebox.adapter.CartListAdapter
import com.example.shoebox.interfaces.OnCartItemClick
import com.example.shoebox.model.DataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AddCartActivity : AppCompatActivity(), OnCartItemClick {
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    var txtItems: TextView? = null
    var txtTotalAmount: TextView? = null
    var txtNoItem: TextView? = null
    var recyclerListCart: RecyclerView? = null
    var cartListAdapter: CartListAdapter? = null
    var onCartItemClick: OnCartItemClick? = null
    var btnCheckOut: Button? = null
    var back: ImageView? = null
    var imgDeleteAll: ImageView? = null
    var types: MutableList<DataModel?>? = null
    var totalPrice = 0.0f
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cart)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        txtItems = findViewById(R.id.txt_items)
        txtNoItem = findViewById(R.id.txt_no_item)
        back = findViewById(R.id.back)
        btnCheckOut = findViewById(R.id.btn_checkOut)
        imgDeleteAll = findViewById(R.id.img_delete_all)
        recyclerListCart = findViewById(R.id.cart_list)
        progressBar = findViewById(R.id.progrss_bar)
        onCartItemClick = this
        txtTotalAmount = findViewById(R.id.txt_total_amount)
        checkOutList
        back?.setOnClickListener {
            finish()
            val intent = Intent(applicationContext, DashBoardActivity::class.java)
            startActivity(intent)
        }
        btnCheckOut?.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            if (types != null) {
                for (dataModel in types!!) {
                    db!!.collection("shoeList").document("orderedList")
                        .collection(FirebaseAuth.getInstance().uid!!).document(
                            dataModel!!.name!!
                        ).set(dataModel).addOnSuccessListener { unused: Void? ->
                            db!!.collection("shoeList").document("CheckoutList").collection(
                                FirebaseAuth.getInstance().uid!!
                            ).document(dataModel.name!!).delete()
                                .addOnSuccessListener {
                                    progressBar?.visibility = View.GONE
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            AccountDetailsActivity::class.java
                                        )
                                    )
                                }.addOnFailureListener {
                                    progressBar?.visibility = View.GONE
                                    Toast.makeText(
                                        applicationContext,
                                        "Sorry, Unable to process",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }.addOnFailureListener {
                            progressBar?.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Sorry, Unable to process",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } else {
                progressBar?.visibility = View.GONE
            }
        }
    }

    // Add all to your list
    private val checkOutList: Unit
        get() {
            progressBar!!.visibility = View.VISIBLE
            val dbCourses = db!!.collection("shoeList").document("CheckoutList").collection(
                FirebaseAuth.getInstance().uid!!
            )
            dbCourses.get().addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                if (!queryDocumentSnapshots.isEmpty) {
                    types = queryDocumentSnapshots.toObjects(DataModel::class.java)
                    // Add all to your list
                    Log.d("FireBaseFireStore", "onSuccess: " + types!![0]!!.name)
                    updateView(types!!)
                } else {
                    progressBar!!.visibility = View.GONE
                    txtNoItem!!.visibility = View.VISIBLE
                    recyclerListCart!!.visibility = View.GONE
                }
            }.addOnFailureListener { e: Exception ->
                progressBar!!.visibility = View.GONE
                Toast.makeText(
                    applicationContext,
                    "Sorry, Unable to proceed \n$e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun updateView(types: List<DataModel?>) {
        progressBar!!.visibility = View.GONE
        val dataModelList: ArrayList<DataModel>? = ArrayList()
        txtItems!!.text = "" + types.size
        for (dataModel in types) {
            if (dataModel!!.count != 0) {
                dataModelList?.add(dataModel)
            }
        }
        cartListAdapter = CartListAdapter(
            this@AddCartActivity,
            dataModelList,
            "ChekoutItem",
            db!!,
            onCartItemClick!!
        )
        recyclerListCart!!.adapter = cartListAdapter
        for (dataModel in types) {
            totalPrice += extractInt(dataModel!!.price, dataModel.count!!).toFloat()
        }
        txtTotalAmount!!.text = "$$totalPrice"
    }

    override fun addItemValue(dataModel: DataModel?) {
        types!!.remove(dataModel)
        types!!.add(dataModel)
        val price: Float? = dataModel?.price?.replace("$", "")?.toFloat()
        val countPrice = price?.times(dataModel!!.count.toString().toFloat())
        if (countPrice != null) {
            totalPrice += countPrice
        }
        txtTotalAmount!!.text = "$$totalPrice"
    }

    override fun deleteItemValue(dataModel: DataModel?) {
        if (dataModel!!.count == 0) {
            types!!.remove(dataModel)
        } else {
            val price: Float? = dataModel.price?.replace("$", "")?.toFloat()
            if (price != null) {
                totalPrice -= price
            }
            txtTotalAmount!!.text = "$$totalPrice"
        }
    }

    companion object {
        fun extractInt(str: String?, count: Int): Int {
            val intValue = str!!.replace("$", "").toInt()
            return intValue * count
        }
    }
}