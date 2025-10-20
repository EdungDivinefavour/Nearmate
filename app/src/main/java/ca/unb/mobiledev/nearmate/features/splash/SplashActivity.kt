package ca.unb.mobiledev.nearmate.features.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ca.unb.mobiledev.nearmate.features.home.HomeActivity
import ca.unb.mobiledev.nearmate.features.landing.LandingActivity
import ca.unb.mobiledev.nearmate.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDelay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            //val intent = Intent(this, LandingActivity::class.java)
            val intent = if (currentUser != null) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, LandingActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, splashDelay)
    }
}