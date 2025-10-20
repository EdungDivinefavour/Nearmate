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
import ca.unb.mobiledev.nearmate.services.UserService

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

        val goToLogin = findViewById<Button>(R.id.lginbutton)

        goToLogin.setOnClickListener {
            userService.login("divinefavour.edung@gmail.com", "qqqqqqqq")
                .thenAccept { user ->
                    if (user != null) {
                        // Runs on a background thread by default
                        runOnUiThread {
                            Toast.makeText(this, "Welcome ${user.firstName}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
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