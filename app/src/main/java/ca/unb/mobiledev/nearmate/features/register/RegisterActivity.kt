package ca.unb.mobiledev.nearmate.features.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.login.LoginActivity

import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import ca.unb.mobiledev.nearmate.services.UserService
import ca.unb.mobiledev.nearmate.features.home.HomeActivity

class RegisterActivity : AppCompatActivity() {
    val userService: UserService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val firstNameET = findViewById<EditText>(R.id.firstNameInput)
        val lastNameET = findViewById<EditText>(R.id.lastNameInput)
        val emailET = findViewById<EditText>(R.id.emailInput)
        val passwordET = findViewById<EditText>(R.id.passwordInput)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val progressBar = findViewById<ProgressBar>(R.id.registerProgressBar)

        registerBtn.setOnClickListener {
            val firstName = firstNameET.text.toString().trim()
            val lastName = lastNameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString()

            when {
                firstName.isEmpty() -> { firstNameET.error = "Required field"; return@setOnClickListener }
                lastName.isEmpty() -> { lastNameET.error = "Required field"; return@setOnClickListener }
                email.isEmpty() -> { emailET.error = "Required field"; return@setOnClickListener }
                password.length < 8 -> {  passwordET.error = "Annoyingly, you need 8 characters here"; return@setOnClickListener }
            }

            val lat = 0.0
            val lng = 0.0

            registerBtn.text = ""
            registerBtn.isEnabled = false
            progressBar.visibility = View.VISIBLE

            userService.register(firstName, lastName, email, password, lat, lng)
                .thenAccept { user ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        registerBtn.text = getString(R.string.login)
                        registerBtn.isEnabled = true

                        if (user != null) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .exceptionally { e ->
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            registerBtn.text = getString(R.string.login)
                            registerBtn.isEnabled = true
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        null
                }
        }
        val goToLogin = findViewById<TextView>(R.id.registerLoginRedirectTV)
        goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}