package com.capstone.crashsnap.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.capstone.crashsnap.R
import com.capstone.crashsnap.ui.auth.LoginActivity
import com.capstone.crashsnap.ui.welcome.WelcomeActivity
@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    private lateinit var welcomeState: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setContentView(R.layout.activity_splashscreen)
        welcomeState = getSharedPreferences("welcomeScreen", MODE_PRIVATE)
        val isFirstTime = welcomeState.getBoolean("firstTime", true)
        if (isFirstTime) {
            val editor = welcomeState.edit()
            editor.putBoolean("firstTime", false)
            editor.apply()
            val intent = Intent(this@SplashscreenActivity, WelcomeActivity::class.java)
            splashScreen.setKeepOnScreenCondition { true }
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SplashscreenActivity, LoginActivity::class.java)
            splashScreen.setKeepOnScreenCondition { true }
            startActivity(intent)
            finish()
        }

    }
}
