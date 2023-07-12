package com.example.shoebox.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shoebox.R

class AccountDetailsActivity : AppCompatActivity() {
    var edtName: EditText? = null
    var edtAddress: EditText? = null
    var etCVV: EditText? = null
    var etEmail: EditText? = null
    var etCardNumber: EditText? = null
    var btnSubmit: TextView? = null
    var okayText: TextView? = null
    var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)
        dialog = Dialog(this@AccountDetailsActivity)
        edtName = findViewById(R.id.et_name)
        edtAddress = findViewById(R.id.et_address)
        etCVV = findViewById(R.id.cardCVCEditText)
        etEmail = findViewById(R.id.et_email)
        etCardNumber = findViewById(R.id.cardNumberEditText)
        btnSubmit = findViewById(R.id.txt_pay)
        btnSubmit?.setOnClickListener(View.OnClickListener { view: View? ->
            if (validate(etEmail, edtName, edtAddress, etCVV, etCardNumber)) {
                successDialog()
            }
        })
    }

    private fun validate(
        email: EditText?,
        edtName: EditText?,
        edtAddress: EditText?,
        etCVV: EditText?,
        etCardNumber: EditText?
    ): Boolean {

        // Extract input from EditText
        val emailInput = email!!.text.toString().trim { it <= ' ' }
        val nameInput = edtName!!.text.toString().trim { it <= ' ' }
        val addressInput = edtAddress!!.text.toString().trim { it <= ' ' }
        val cvvInput = etCVV!!.text.toString().trim { it <= ' ' }
        val cardInput = etCardNumber!!.text.toString().trim { it <= ' ' }

        // if the email input field is empty
        return if (emailInput.isEmpty()) {
            email.error = "Field can not be empty"
            false
        } else if (nameInput.isEmpty()) {
            edtName.error = "Field can not be empty"
            false
        } else if (addressInput.isEmpty()) {
            edtAddress.error = "Field can not be empty"
            false
        } else if (cvvInput.isEmpty()) {
            etCVV.error = "Field can not be empty"
            false
        } else if (cardInput.isEmpty()) {
            etCardNumber.error = "Field can not be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.error = "Please enter a valid email address"
            false
        } else {
            email.error = null
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
}