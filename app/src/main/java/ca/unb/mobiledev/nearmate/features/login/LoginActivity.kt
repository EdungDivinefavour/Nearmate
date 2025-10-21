package ca.unb.mobiledev.nearmate.features.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.home.HomeActivity
import ca.unb.mobiledev.nearmate.services.UserService
import android.widget.EditText
import android.widget.ProgressBar
import android.view.View
import android.widget.TextView
import ca.unb.mobiledev.nearmate.features.forgotpassword.ForgotPasswordActivity


class LoginActivity : AppCompatActivity() {
    val userService: UserService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailET = findViewById<EditText>(R.id.emailLoginInput)
        val passwordET = findViewById<EditText>(R.id.passwordLoginInput)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val goToForgotPassword = findViewById<TextView>(R.id.loginForgotPasswordRedirectTV)
        val progressBar = findViewById<ProgressBar>(R.id.loginProgressBar)

        loginBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString()

            when {
                email.isEmpty() -> { emailET.error = "Email required"; return@setOnClickListener }
                password.isEmpty() -> { passwordET.error = "Password required"; return@setOnClickListener }
            }

            loginBtn.text = ""
            loginBtn.isEnabled = false
            progressBar.visibility = View.VISIBLE

            userService.login(email, password)
                .thenAccept { user ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        loginBtn.text = getString(R.string.login)
                        loginBtn.isEnabled = true

                        if (user != null) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .exceptionally { e ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        loginBtn.text = getString(R.string.login)
                        loginBtn.isEnabled = true
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
        }

        goToForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}