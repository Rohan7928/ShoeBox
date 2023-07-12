package com.example.shoebox.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shoebox.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    var btnLogo: Button? = null
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLogo = findViewById(R.id.homeButton)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                auth = FirebaseAuth.getInstance()
                if (auth!!.currentUser != null) {
                    // User is signed in (getCurrentUser() will be null if not signed in)
                    val intent = Intent(this@SplashActivity, DashBoardActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }, 3000)
    }
}