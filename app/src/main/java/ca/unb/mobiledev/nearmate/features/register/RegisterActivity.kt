package ca.unb.mobiledev.nearmate.features.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.login.LoginActivity

import android.widget.Button
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

        val firstNameInputField = findViewById<View>(R.id.firstNameInputField)
        val lastNameInputField = findViewById<View>(R.id.lastNameInputField)
        val emailInputField = findViewById<View>(R.id.emailInputField)
        val passwordInputField = findViewById<View>(R.id.passwordInputField)
        
        val firstNameET = firstNameInputField.findViewById<EditText>(R.id.inputEditText)
        val lastNameET = lastNameInputField.findViewById<EditText>(R.id.inputEditText)
        val emailET = emailInputField.findViewById<EditText>(R.id.inputEditText)
        val passwordET = passwordInputField.findViewById<EditText>(R.id.inputEditText)
        
        // Set labels and hints
        firstNameInputField.findViewById<TextView>(R.id.inputLabel).text = getString(R.string.first_name)
        firstNameET.hint = getString(R.string.enter_your_firstname)
        firstNameET.inputType = android.text.InputType.TYPE_CLASS_TEXT

        lastNameInputField.findViewById<TextView>(R.id.inputLabel).text = getString(R.string.last_name)
        lastNameET.hint = getString(R.string.enter_your_lastname)
        lastNameET.inputType = android.text.InputType.TYPE_CLASS_TEXT
        
        emailInputField.findViewById<TextView>(R.id.inputLabel).text = getString(R.string.email)
        emailET.hint = getString(R.string.enter_your_email_address)
        emailET.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        
        passwordInputField.findViewById<TextView>(R.id.inputLabel).text = getString(R.string.password)
        passwordET.hint = getString(R.string.enter_your_password)
        passwordET.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT
        
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