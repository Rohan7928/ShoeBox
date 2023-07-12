package com.example.shoebox.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.shoebox.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoebox.R
import java.util.regex.Pattern

class UpdateSignUpActivity : AppCompatActivity() {
    var emailTextView: EditText? = null
    var passwordTextView: EditText? = null
    var confirmPasswordEditText: EditText? = null
    var edtName: EditText? = null
    var edtMobile: EditText? = null
    var btn_signUp: Button? = null
    var progressbar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    var upDateLayout: ConstraintLayout? = null
    var layoutLogout: LinearLayoutCompat? = null
    var ivPasswordVisible: ImageView? = null
    var ivRePasswordVisible: ImageView? = null
    var imgBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updatesignup)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        emailTextView = findViewById(R.id.et_email)
        passwordTextView = findViewById(R.id.et_password)
        edtName = findViewById(R.id.et_name)
        edtMobile = findViewById(R.id.et_mobile)
        imgBack = findViewById(R.id.back)
        confirmPasswordEditText = findViewById(R.id.et_repassword)
        btn_signUp = findViewById(R.id.btn_register)
        upDateLayout = findViewById(R.id.upDate_layout)
        layoutLogout = findViewById(R.id.layout_logout)
        //getDataFromFirebase
        dataFromFireStore
        progressbar = findViewById(R.id.progressbar)
        ivPasswordVisible = findViewById(R.id.iv_password_visible)
        ivRePasswordVisible = findViewById(R.id.iv_re_password_visible)
        imgBack?.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext, DashBoardActivity::class.java))
        }
        ivPasswordVisible?.setOnClickListener {
            if (passwordTextView?.transformationMethod == PasswordTransformationMethod.getInstance()) {
                ivPasswordVisible?.setImageResource(R.drawable.ic_eye)
                //Show Password
                passwordTextView?.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                ivPasswordVisible?.setImageResource(R.drawable.ic_eye_off)
                //Hide Password
                passwordTextView?.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        ivRePasswordVisible?.setOnClickListener { _: View? ->
            if (confirmPasswordEditText?.transformationMethod == PasswordTransformationMethod.getInstance()) {
                ivRePasswordVisible?.setImageResource(R.drawable.ic_eye)
                //Show Password
                confirmPasswordEditText?.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                ivRePasswordVisible?.setImageResource(R.drawable.ic_eye_off)
                //Hide Password
                confirmPasswordEditText?.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        layoutLogout?.setOnClickListener {
            mAuth!!.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_signUp?.setOnClickListener {
            if (validateEmail(emailTextView) && validatePassword(passwordTextView) && matchPassword(
                    passwordTextView, confirmPasswordEditText
                )
            ) {
                updateRegisteredUser()
            } else {
                Toast.makeText(
                    this@UpdateSignUpActivity, "Sorry, Unable to process", Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun updateRegisteredUser() {
        progressbar?.visibility = View.VISIBLE
        val email: String = emailTextView?.text.toString()
        val password: String = passwordTextView?.text.toString()
        val mobile: String = edtMobile?.text.toString()
        val name: String = edtName?.text.toString()
        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email!!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!!", Toast.LENGTH_SHORT).show()
            return
        }
        if (mobile.length < 9) {
            Toast.makeText(this, "Please valid mobile number!!", Toast.LENGTH_SHORT).show()
            return
        }

        // create new user or register new user
        mAuth?.updateCurrentUser(FirebaseAuth.getInstance().currentUser!!)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@UpdateSignUpActivity, "Update successful!", Toast.LENGTH_SHORT
                    ).show()
                    updateDataToFireStore(email, password, name, mobile)
                    progressbar?.visibility = View.GONE
                } else {
                    // Registration failed
                    Toast.makeText(
                        this@UpdateSignUpActivity,
                        "Updation failed!!\" + \" Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressbar?.visibility = View.GONE
                }
            }
    }

    private fun updateDataToFireStore(
        email: String, password: String, name: String, mobile: String
    ) {

        val dbCourses = db?.collection("customers")
            ?.document(FirebaseAuth.getInstance().currentUser?.uid.toString())

        val userModel = UserModel(name=name, email=email, password=password, mobile = mobile)

        dbCourses?.set(userModel)?.addOnSuccessListener {

            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }?.addOnFailureListener { e ->
            Toast.makeText(
                applicationContext, "Fail to add course \n$e", Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun validateEmail(email: EditText?): Boolean {
        // Extract input from EditText
        val emailInput = email!!.text.toString().trim { it <= ' ' }
        // if the email input field is empty
        return if (emailInput.isEmpty()) {
            email.error = "Field can not be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.error = "Please enter a valid email address"
            false
        } else {
            email.error = null
            true
        }
    }

    private fun validatePassword(password: EditText?): Boolean {
        val passwordInput = password!!.text.toString().trim { it <= ' ' }
        // if password field is empty
        // it will display error message "Field can not be empty"
        return if (passwordInput.isEmpty()) {
            password.error = "Field can not be empty"
            false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password.error = "Password is too weak"
            false
        } else {
            password.error = null
            true
        }
    }

    private fun matchPassword(
        passwordTextView: EditText?, confirmPasswordEditText: EditText?
    ): Boolean {
        val password = passwordTextView!!.text.toString().trim { it <= ' ' }
        val confirmPassword = confirmPasswordEditText!!.text.toString().trim { it <= ' ' }
        return password == confirmPassword
    }

    private val dataFromFireStore: Unit
        get() {
            if (isNetworkAvailable) {
                FirebaseAuth.getInstance().currentUser!!.email
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
            emailTextView?.setText(userModel.email)
            edtName?.setText(userModel.name)
            edtMobile?.setText(userModel.mobile)
            passwordTextView?.setText(userModel.password)
            confirmPasswordEditText?.setText(userModel.password)
        }
    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            "^" + "(?=.*[@#$%^&+=])" +  // at least 1 special character
                    "(?=\\S+$)" +  // no white spaces
                    ".{4,}" +  // at least 4 characters
                    "$"
        )
    }
}