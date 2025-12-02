package ca.unb.mobiledev.nearmate.features.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.services.LocationService
import ca.unb.mobiledev.nearmate.services.UserService
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var locationService: LocationService
    private val userService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        locationService = LocationService(this)
        locationService.requestLocationPermission()

        locationService.startLocationUpdates { lat, lng ->
            userService.updateLocation(lat, lng)
                .exceptionally { e ->
                    // Silently log/ignore location update failures
                    null
                }
        }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(this)
        viewPager.isUserInputEnabled = false

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            val index = when (item.itemId) {
                R.id.nav_user_list -> 0
                R.id.nav_map -> 1
                R.id.nav_chat_list -> 2
                R.id.nav_profile -> 3
                else -> 0
            }

            viewPager.currentItem = index
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationService.stopLocationUpdates()
    }
}