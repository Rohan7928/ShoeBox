package com.example.shoebox.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.shoebox.adapter.ImageSlideAdapter
import com.example.shoebox.adapter.ItemsListAdapter
import com.example.shoebox.model.DataModel
import com.example.shoebox.model.ImagesModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.shoebox.R
import me.relex.circleindicator.CircleIndicator


class DashBoardActivity : AppCompatActivity() {
    var recyclerViewList: RecyclerView? = null
    var itemsListAdapter: ItemsListAdapter? = null
    var progressBar: ProgressBar? = null
    var itemListLayout: ConstraintLayout? = null
    var txtNoItem: TextView? = null
    var imgLogo: ImageView? = null
    var fbCart: FloatingActionButton? = null
    lateinit var viewPagerAdapter: ImageSlideAdapter
    lateinit var indicator: CircleIndicator
    lateinit var viewpager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)
        progressBar = findViewById(R.id.progrss_bar)
        txtNoItem = findViewById(R.id.txt_no_item)
        itemListLayout = findViewById(R.id.itemList_layout)
        recyclerViewList = findViewById(R.id.recyclerList)
        imgLogo = findViewById(R.id.img_logo)
        fbCart = findViewById(R.id.fb_cart)

        viewpager = findViewById(R.id.viewpager)
        progressBar?.visibility = View.VISIBLE
        //Google Singin


        imgLogo?.setOnClickListener {
            val intent = Intent(applicationContext, UpdateSignUpActivity::class.java)
            startActivity(intent)
        }
        fbCart?.setOnClickListener {
            val intent = Intent(applicationContext, AddCartActivity::class.java)
            startActivity(intent)
        }
        val db = FirebaseFirestore.getInstance()
        val dataModels = ArrayList<DataModel>()
        getAdsImages(db)
        if (isNetworkAvailable) {
            db.collection("shoeList").document("category").collection("men").get()
                .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        progressBar?.visibility = View.GONE
                        val types = queryDocumentSnapshots.toObjects(
                            DataModel::class.java
                        )
                        dataModels.addAll(types)
                        Log.d("FireBaseFireStore", "onSuccess: " + dataModels[0].image)
                        itemsListAdapter =
                            ItemsListAdapter(applicationContext, dataModels, "ItemsListActivity")
                        recyclerViewList?.layoutManager = GridLayoutManager(
                            this@DashBoardActivity,
                            2
                        )

                        recyclerViewList?.adapter = itemsListAdapter
                    } else {
                        progressBar?.visibility = View.GONE
                        txtNoItem?.visibility = View.VISIBLE
                        recyclerViewList?.visibility = View.GONE
                    }
                }.addOnFailureListener { e: Exception ->
                    progressBar?.visibility = View.GONE
                    txtNoItem?.visibility = View.VISIBLE
                    recyclerViewList?.visibility = View.GONE
                    Toast.makeText(this@DashBoardActivity, "" + e.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.i("FireBaseFireStoreFailed", "" + e)
                }
        } else {
            txtNoItem?.visibility = View.VISIBLE
            recyclerViewList?.setVisibility(View.GONE)
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAdsImages(db: FirebaseFirestore) {
        if (isNetworkAvailable) {

            val rootRef = FirebaseFirestore.getInstance()
            val userImagesRef = rootRef.collection("images_collection")
            val docRef = userImagesRef.document("images_document")
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val documentClass: ImagesModel? =
                            document.toObject(ImagesModel::class.java)
                        val images: List<String>? = documentClass?.images
                        getImagesForAds(images)
                    } else {
                        Log.d("TAG", "No such document")
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.exception)
                }
            }
        } else {
            txtNoItem?.visibility = View.VISIBLE
            recyclerViewList?.visibility = View.GONE
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImagesForAds(dataModels: List<String>?) {
        val imagesModel: ArrayList<String> = arrayListOf()
        if (dataModels != null) {
            for (data in dataModels) {
                imagesModel.add(data)
            }
        }
        setImageSlider(imagesModel)

    }

    private fun setImageSlider(imagesModel: ArrayList<String>) {
        imagesModel?.let {
            viewPagerAdapter = ImageSlideAdapter(applicationContext, it)
            viewpager.adapter = viewPagerAdapter
            indicator = findViewById(R.id.indicator)
            indicator.setViewPager(viewpager)
        }
    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).create()
        alert.setTitle("Exit")
        alert.setMessage("Are you sure you want to exit?")
        alert.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)
        alert.setButton(
            DialogInterface.BUTTON_POSITIVE,
            "OK"
        ) { _: DialogInterface?, _: Int -> finishAffinity() }
        alert.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            "Cancel"
        ) { _: DialogInterface?, _: Int -> alert.dismiss() }
        alert.show()
    }



}