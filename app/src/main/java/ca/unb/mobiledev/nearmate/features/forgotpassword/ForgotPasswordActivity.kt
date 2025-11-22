package ca.unb.mobiledev.nearmate.features.forgotpassword

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.services.UserService

class ForgotPasswordActivity : AppCompatActivity() {
    val userService: UserService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInputField = findViewById<View>(R.id.emailInputField)
        val emailET = emailInputField.findViewById<EditText>(R.id.inputEditText)
        
        // Hide label for forgot password (we have "What's your email?" text instead)
        emailInputField.findViewById<TextView>(R.id.inputLabel).visibility = View.GONE
        emailET.hint = getString(R.string.enter_your_email_address)
        emailET.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        
        val sendBtn = findViewById<Button>(R.id.sendButton)
        val progressBar = findViewById<ProgressBar>(R.id.forgotProgressBar)

        sendBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            if (email.isEmpty()) {
                emailET.error = "Input Required"
                return@setOnClickListener
            }

            sendBtn.text = ""
            sendBtn.isEnabled = false
            progressBar.visibility = View.VISIBLE

            userService.sendPasswordResetEmail(email)
                .thenAccept { success ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        sendBtn.text = getString(R.string.send)
                        sendBtn.isEnabled = true

                        if (success) {
                            Toast.makeText(
                                this,
                                "Your reset link was sent to $email !",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Oops! Something went wrong.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .exceptionally { e ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        sendBtn.text = getString(R.string.send)
                        sendBtn.isEnabled = true
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
        }
    }
}