package com.example.shoebox.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.shoebox.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    var btnLogin: Button? = null
    var btnSignUp: Button? = null
    var edtName: EditText? = null
    var edtPassword: EditText? = null
    private var mAuth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    var loginLayout: ConstraintLayout? = null
    var ivPasswordVisible: ImageView? = null
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var btSignIn: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        edtName = findViewById(R.id.editTextEmail)
        edtPassword = findViewById(R.id.editTextPassword)
        btnLogin = findViewById(R.id.btnLoginButton)
        btnSignUp = findViewById(R.id.btnSignUpButton)
        progressBar = findViewById(R.id.progressBar)
        loginLayout = findViewById(R.id.login_layout)
        btSignIn = findViewById(R.id.bt_sign_in)
        ivPasswordVisible = findViewById(R.id.iv_password_visible)
        mAuth = FirebaseAuth.getInstance()

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("833868470311-o0q3uddn9ohb0re1909ktce21d0i83fb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, googleSignInOptions)
        btSignIn.setOnClickListener { // Initialize sign in intent
            val intent: Intent = googleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            startActivity(
                Intent(
                    applicationContext,
                    DashBoardActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }


        ivPasswordVisible?.setOnClickListener { view: View? ->
            if ((edtPassword?.transformationMethod ==
                        PasswordTransformationMethod.getInstance()
                        )
            ) {
                ivPasswordVisible?.setImageResource(R.drawable.ic_eye)
                //Show Password
                edtPassword?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                ivPasswordVisible?.setImageResource(R.drawable.ic_eye_off)
                //Hide Password
                edtPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        btnLogin?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val email = edtName?.text.toString()
                val password = edtPassword?.text.toString()
                if (validateEmail(edtName) && validatePassword(edtPassword)) {
                    if (isNetworkAvailable) {
                        if (email.contains("Admin@gmail.com")) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            startActivity(Intent(this@LoginActivity, AdminDashBoard::class.java))
                        } else {
                            mAuth?.signInWithEmailAndPassword(email, password)
                                ?.addOnCompleteListener(
                                    OnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            checkIfEmailVerified(email, password)
                                        } else {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Login failed! Please try again later!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            progressBar?.visibility = View.GONE
                                        }
                                    })?.addOnFailureListener { e ->
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "" + e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            private fun checkIfEmailVerified(email: String, password: String) {
                val user = FirebaseAuth.getInstance().currentUser
                progressBar?.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
                /*  if (user != null) {
                    if (user.isEmailVerified()) {

                    } else {
                        progressBar.setVisibility(View.GONE);
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "User Not Verified!", Toast.LENGTH_SHORT).show();
                        //FirebaseAuth.getInstance().signOut();
                    }
                }*/
            }
        })
        btnSignUp?.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private val isNetworkAvailable: Boolean
        private get() {
            val connectivityManager: ConnectivityManager? =
                getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    private fun validateEmail(email: EditText?): Boolean {
        // Extract input from EditText
        val emailInput = email?.text.toString().trim { it <= ' ' }
        // if the email input field is empty
        return if (emailInput.isEmpty()) {
            email?.error = "Field can not be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email?.error = "Please enter a valid email address"
            false
        } else {
            email?.error = null
            true
        }
    }

    private fun validatePassword(password: EditText?): Boolean {
        val passwordInput = password?.text.toString().trim { it <= ' ' }
        return if (passwordInput.isEmpty()) {
            password?.error = "Field can not be empty"
            false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password?.error = "Password is too weak"
            false
        } else {
            password?.error = null
            true
        }
    }

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            ("^" + "(?=.*[@#$%^&+=])" +  // at least 1 special character
                    "(?=\\S+$)" +  // no white spaces
                    ".{4,}" +  // at least 4 characters
                    "$")
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            // check condition
            if (signInAccountTask.isSuccessful) {
                // When google sign in successful initialize string
                val s = "Google sign in successful"
                // Display Toast
                displayToast(s)
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                // Check condition
                                if (task.isSuccessful) {
                                    // When task is successful redirect to profile activity
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            DashBoardActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    // Display Toast
                                    displayToast("Firebase authentication successful")
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast(
                                        "Authentication Failed :" + task.exception?.message
                                    )
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }else
            {
                displayToast(
                "Authentication Failed Else"
            )

            }
        }
    }

    private fun displayToast(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
    }

}