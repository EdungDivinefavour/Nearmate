package ca.unb.mobiledev.nearmate.features.landing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.R.*
import ca.unb.mobiledev.nearmate.features.home.HomeActivity
import ca.unb.mobiledev.nearmate.features.login.LoginActivity

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_landing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val goToLogin = findViewById<Button>(R.id.goToLogin)
        goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}