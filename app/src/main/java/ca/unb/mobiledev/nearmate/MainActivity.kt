package ca.unb.mobiledev.nearmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.nearmate.features.home.HomeActivity
import ca.unb.mobiledev.nearmate.services.LocationServiceImpl
import ca.unb.mobiledev.nearmate.services.UserService
import ca.unb.mobiledev.nearmate.services.UserServiceImpl

class MainActivity : AppCompatActivity() {
    val userService: UserService = UserServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        LocationServiceImpl(this).requestLocationPermission()

        val testLogin: Button = findViewById<Button>(R.id.test_button)
        val testSignup: Button = findViewById<Button>(R.id.test_singup)
        val testFindUsers: Button = findViewById<Button>(R.id.test_find)
        val testHomegoing: Button = findViewById<Button>(R.id.test_go_home)

        testLogin.setOnClickListener {
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

        testSignup.setOnClickListener {
            userService.register(
                "Bluuuhh",
                "Name",
                "serialize.edung@gmail.com",
                "qqqqqqqq",
                34.234234,
                3.4234324
            )
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

        testFindUsers.setOnClickListener {
            userService.findNearLocation(
                45.0000,
                -66.0000,
                5.0,
            )
                .thenAccept { users ->
                    if (users != null) {
                        // Runs on a background thread by default
                        runOnUiThread {
                            Toast.makeText(this, "Buuuuuuh", Toast.LENGTH_SHORT).show()
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

        testHomegoing.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}