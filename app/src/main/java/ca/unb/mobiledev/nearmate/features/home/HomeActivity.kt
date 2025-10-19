package ca.unb.mobiledev.nearmate.features.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.services.LocationService
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        LocationService(this).requestLocationPermission()

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
}