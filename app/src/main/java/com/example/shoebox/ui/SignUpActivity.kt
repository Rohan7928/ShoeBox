package com.example.shoebox.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.shoebox.R
import com.example.shoebox.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    var emailTextView: EditText? = null
    var passwordTextView: EditText? = null
    var confirmPasswordEditText: EditText? = null
    var etNameTextView: EditText? = null
    var etMobile: EditText? = null
    var btn_signUp: Button? = null
    var progressbar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    var signUpLayout: ConstraintLayout? = null
    var ivPasswordVisible: ImageView? = null
    var ivRePasswordVisible: ImageView? = null
    var imgBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        emailTextView = findViewById(R.id.et_email)
        etNameTextView = findViewById(R.id.et_name)
        etMobile = findViewById(R.id.et_mobile)
        passwordTextView = findViewById(R.id.et_password)
        confirmPasswordEditText = findViewById(R.id.et_repassword)
        btn_signUp = findViewById(R.id.btn_register)
        signUpLayout = findViewById(R.id.signUp_layout)
        ivPasswordVisible = findViewById(R.id.iv_password_visible)
        imgBack = findViewById(R.id.back_signUp)
        ivRePasswordVisible = findViewById(R.id.iv_re_password_visible)
        progressbar = findViewById(R.id.progressbar)
        imgBack?.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
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
        ivRePasswordVisible?.setOnClickListener {
            if (confirmPasswordEditText?.transformationMethod == PasswordTransformationMethod.getInstance()) {
                ivRePasswordVisible?.setImageResource(R.drawable.ic_eye)
                //Show Password
                confirmPasswordEditText?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                ivRePasswordVisible?.setImageResource(R.drawable.ic_eye_off)
                //Hide Password
                confirmPasswordEditText?.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        // Set on Click Listener on Registration button
        btn_signUp?.setOnClickListener {
            if (validateEmail(emailTextView) && validatePassword(passwordTextView) && matchPassword(
                    passwordTextView,
                    confirmPasswordEditText
                )
            ) registerNewUser() else {
                Toast.makeText(this@SignUpActivity, "Sorry, Unable to process", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun matchPassword(
        passwordTextView: EditText?,
        confirmPasswordEditText: EditText?
    ): Boolean {
        val password = passwordTextView!!.text.toString().trim { it <= ' ' }
        val confirmPassword = confirmPasswordEditText!!.text.toString().trim { it <= ' ' }
        return password == confirmPassword
    }

    private fun registerNewUser() {
        progressbar!!.visibility = View.VISIBLE
        val email: String
        val password: String
        val name: String
        val mobile: String
        email = emailTextView!!.text.toString()
        password = passwordTextView!!.text.toString()
        name = etNameTextView!!.text.toString()
        mobile = etMobile!!.text.toString()
        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email!!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password!!", Toast.LENGTH_SHORT).show()
            return
        }

        // create new user or register new user
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful) {
                        addDataToFireStore(email, password, name, mobile)
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressbar!!.visibility = View.GONE
                        // if the user created intent to login activity
                    } else {
                        // Registration failed
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registration failed!!\" + \" Please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressbar!!.visibility = View.GONE
                    }
                }
            })
    }

    private fun addDataToFireStore(email: String, password: String, name: String, mobile: String) {
        val dbCourses = db!!.collection("customers").document(FirebaseAuth.getInstance().uid!!)
            .collection(email)

        // adding our data to our courses object class.
        val userModel = UserModel(name, email, password, mobile)

        // below method is use to add data to Firebase Firestore.
        dbCourses.add(userModel)
            .addOnSuccessListener { // after the data addition is successful
                // we are displaying a success toast message.
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(applicationContext, "Fail to add course \n$e", Toast.LENGTH_SHORT)
                    .show()
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

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            "^" + "(?=.*[@#$%^&+=])" +  // at least 1 special character
                    "(?=\\S+$)" +  // no white spaces
                    ".{4,}" +  // at least 4 characters
                    "$"
        )
    }
}