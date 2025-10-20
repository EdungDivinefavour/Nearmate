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

//added imports

import android.widget.EditText


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
        val emailET    = findViewById<EditText>(R.id.emailLoginInput)
        val passwordET = findViewById<EditText>(R.id.passwordLoginInput)
        val loginBtn   = findViewById<Button>(R.id.loginButton)

        loginBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString()

            // not null requirement
            when {
                email.isEmpty() -> { emailET.error = "Email required"; return@setOnClickListener }
                password.isEmpty() -> { passwordET.error = "Password required"; return@setOnClickListener }
            }

            userService.login(email, password)
                .thenAccept { user ->
                    runOnUiThread {
                        if (user != null) {
                            Toast.makeText(this, "Welcome ${user.firstName}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
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

//        testSignup.setOnClickListener {
//            userService.register(
//                "Bluuuhh",
//                "Name",
//                "serialize.edung@gmail.com",
//                "qqqqqqqq",
//                34.234234,
//                3.4234324
//            )
//                .thenAccept { user ->
//                    if (user != null) {
//                        // Runs on a background thread by default
//                        runOnUiThread {
//                            Toast.makeText(this, "Welcome ${user.firstName}", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        runOnUiThread {
//                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .exceptionally { e ->
//                    runOnUiThread {
//                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//                    null
//                }
//        }
//
//        testFindUsers.setOnClickListener {
//            userService.findNearLocation(
//                45.0000,
//                -66.0000,
//                5.0,
//            )
//                .thenAccept { users ->
//                    if (users != null) {
//                        // Runs on a background thread by default
//                        runOnUiThread {
//                            Toast.makeText(this, "Buuuuuuh", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        runOnUiThread {
//                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .exceptionally { e ->
//                    runOnUiThread {
//                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//                    null
//                }
//        }
    }

}