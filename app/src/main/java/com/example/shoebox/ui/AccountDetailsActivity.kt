package com.example.shoebox.ui

import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoebox.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoebox.R

class AccountDetailsActivity : AppCompatActivity() {
    var edtName: EditText? = null
    var edtMobile: EditText? = null
    var edtAddress: EditText? = null
    var etCVV: EditText? = null
    var etEmail: EditText? = null
    var etCardNumber: EditText? = null
    var btnSubmit: TextView? = null
    var okayText: TextView? = null
    var dialog: Dialog? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)
        dialog = Dialog(this@AccountDetailsActivity)
        edtName = findViewById(R.id.et_name)
        edtAddress = findViewById(R.id.et_address)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        etCVV = findViewById(R.id.cardCVCEditText)
        etEmail = findViewById(R.id.et_email)
        edtMobile = findViewById(R.id.et_mobile)
        etCardNumber = findViewById(R.id.cardNumberEditText)
        btnSubmit = findViewById(R.id.txt_pay)
        //getDataFromFirebase
        dataFromFireStore
        btnSubmit?.setOnClickListener { view: View? ->
            if (validate(etEmail, edtName, edtAddress, etCVV, etCardNumber, edtMobile)) {
                successDialog()
            }
        }
    }

    private val dataFromFireStore: Unit
        get() {
            if (isNetworkAvailable) {
                val email = FirebaseAuth.getInstance().currentUser!!.email
                val dbCourses = db?.collection("customers")
                    ?.document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                dbCourses?.get()?.addOnSuccessListener {

                    val email: String = it.data?.get("email").toString()
                    val name: String = it.data?.get("name").toString()
                    val mobile: String = it.data?.get("mobile").toString()
                    val password: String = it.data?.get("password").toString()
                    val userModel =
                        UserModel(name = name, email = email, password = password, mobile = mobile)
                    updateView(userModel)
                    Log.d("FireBaseFireStore", "onSuccess: " + userModel?.name)
                }?.addOnFailureListener { e ->
                    Toast.makeText(
                        applicationContext, "Fail to add course \n$e", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "No Internet", Toast.LENGTH_SHORT).show()
            }
        }

    private fun updateView(userModel: UserModel?) {
        if (userModel != null) {
            etEmail?.setText(userModel.email)
            edtName?.setText(userModel.name)
            edtMobile?.setText(userModel.mobile)
        }
    }

    private fun validate(
        email: EditText?,
        edtName: EditText?,
        edtAddress: EditText?,
        etCVV: EditText?,
        etCardNumber: EditText?,
        edtMobile: EditText?
    ): Boolean {

        // Extract input from EditText
        val emailInput = email?.text.toString().trim { it <= ' ' }
        val nameInput = edtName?.text.toString().trim { it <= ' ' }
        val addressInput = edtAddress?.text.toString().trim { it <= ' ' }
        val edtMobileInput = edtMobile?.text.toString().trim { it <= ' ' }
        val cvvInput = etCVV?.text.toString().trim { it <= ' ' }
        val cardInput = etCardNumber?.text.toString().trim { it <= ' ' }

        // if the email input field is empty
        return if (emailInput.isEmpty()) {
            email?.error = "Field can not be empty"
            false
        } else if (nameInput.isEmpty()) {
            edtName?.error = "Field can not be empty"
            false
        } else if (addressInput.isEmpty()) {
            edtAddress?.error = "Field can not be empty"
            false
        } else if (cvvInput.isEmpty()) {
            etCVV?.error = "Field can not be empty"
            false
        } else if(cvvInput.length < 2)
        {
            etCVV?.error = "Please enter valid CVV"
            false
        }
        else if (cardInput.isEmpty()) {
            etCardNumber?.error = "Field can not be empty"
            false
        }
        else if (cardInput.length < 9) {
            etCardNumber?.error = "Please enter valid card detail"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email?.error = "Please enter a valid email address"
            false
        } else if (edtMobileInput.isEmpty() && edtMobileInput.length < 9) {
            edtMobile?.error = "Please enter a valid mobile number"
            false
        } else {
            email?.error = null
            true
        }
    }

    private fun successDialog() {
        dialog!!.setContentView(R.layout.custom_dialog)
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
        dialog?.window?.attributes?.windowAnimations = R.style.animation
        okayText = dialog!!.findViewById(R.id.okay_text)
        okayText?.setOnClickListener { v: View? ->
            dialog?.dismiss()
            finish()
            startActivity(Intent(applicationContext, DashBoardActivity::class.java))
        }
        dialog!!.show()
    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
}