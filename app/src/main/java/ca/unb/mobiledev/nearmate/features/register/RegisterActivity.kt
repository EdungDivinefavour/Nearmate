package ca.unb.mobiledev.nearmate.features.register

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.login.LoginActivity

//added imports
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ca.unb.mobiledev.nearmate.services.UserService
import ca.unb.mobiledev.nearmate.features.home.HomeActivity

class RegisterActivity : AppCompatActivity() {

    val userService: UserService = UserService() //fire base init for kt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //input variables

        val firstNameET = findViewById<EditText>(R.id.firstNameInput)   //registration first name
        val lastNameET = findViewById<EditText>(R.id.lastNameInput)    //registration last name
        val emailET = findViewById<EditText>(R.id.emailInput)       //registration email
        val passwordET = findViewById<EditText>(R.id.passwordInput)    //registration password

        val registerBtn = findViewById<Button>(R.id.registerButton)     //create button


        registerBtn.setOnClickListener {                                    //create listener for button and take inputs
            val firstName = firstNameET.text.toString().trim()
            val lastName = lastNameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString()

            //not null requirement
            when {
                firstName.isEmpty() -> {
                    firstNameET.error = "Required field"; return@setOnClickListener
                }

                lastName.isEmpty() -> {
                    lastNameET.error = "Required field"; return@setOnClickListener
                }

                email.isEmpty() -> {
                    emailET.error = "Required field"; return@setOnClickListener
                }

                password.length < 8 -> {
                    passwordET.error =
                        "Annoyingly, you need 8 characters here"; return@setOnClickListener
                } //require 8 characters to be annoying
            }
            //lat long placeholders
            val lat = 0.0
            val lng = 0.0

            //busy wait for input
            registerBtn.isEnabled = false
            //take inputs from text feilds and save them
            userService.register(firstName, lastName, email, password, lat, lng)
                .thenAccept { user ->
                    runOnUiThread {
                        registerBtn.isEnabled = true
                        if (user != null) {
                            Toast.makeText(this, "Welcome ${user.firstName}", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .exceptionally { e ->
                        runOnUiThread {
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