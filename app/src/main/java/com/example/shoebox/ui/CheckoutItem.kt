package com.example.shoebox.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoebox.adapter.ItemsListAdapter
import com.example.shoebox.model.DataModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoebox.R

class CheckoutItem : AppCompatActivity() {
    var btnBuy: CardView? = null
    var shoeName: AppCompatTextView? = null
    var shoeDesc: AppCompatTextView? = null
    var shoePrice: AppCompatTextView? = null
    var shoeNametxt: String? = null
    var shoePricetxt: String? = null
    var shoeDesctxt: String? = null
    var shoeImagetxt: String? = null
    var shoeId = 0
    lateinit var imgShoe: AppCompatImageView
    var imgBack: AppCompatImageView? = null
    var layoutCheckOut: ConstraintLayout? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chekout_item)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        btnBuy = findViewById(R.id.card_buy)
        layoutCheckOut = findViewById(R.id.layout_checkOut)
        shoeName = findViewById(R.id.txt_shoeName)
        shoePrice = findViewById(R.id.txt_price)
        shoeDesc = findViewById(R.id.txt_shoeDesc)
        imgShoe = findViewById(R.id.img_shoe)
        imgBack = findViewById(R.id.back)
        shoeId = intent.getIntExtra("shoeId", 0)
        shoeNametxt = intent.getStringExtra("itemName")
        shoePricetxt = intent.getStringExtra("itemPrice")
        shoeDesctxt = intent.getStringExtra("itemDesc")
        shoeImagetxt = intent.getStringExtra("itemImage")
        shoeName?.text = shoeNametxt
        shoePrice?.text = shoePricetxt
        shoeDesc?.text = shoeDesctxt
        Glide.with(this).load(shoeImagetxt).placeholder(R.mipmap.one).into(imgShoe)
        imgBack?.setOnClickListener { onBack() }
        btnBuy?.setOnClickListener {
            addItemToCart(
                DataModel(
                    shoeNametxt, shoePricetxt, shoeDesctxt, shoeImagetxt, shoeId, 1
                )
            )
        }
    }

    private fun addItemToCart(dataModel: DataModel) {
        val dbCourses = db?.collection("shoeList")?.document("CheckoutList")?.collection(
            FirebaseAuth.getInstance().uid.toString()
        )
        dbCourses?.document(dataModel?.name.toString())?.set(dataModel)
            ?.addOnCompleteListener { task: Task<Void?>? ->
                Toast.makeText(
                    applicationContext, "Added to Card", Toast.LENGTH_SHORT
                ).show()
            }?.addOnFailureListener { e: Exception ->
                Toast.makeText(
                    applicationContext, "" + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onBackPressed() {
        onBack()
    }

    private fun onBack() {
        finish()
        startActivity(Intent(this@CheckoutItem, DashBoardActivity::class.java))
    }
}