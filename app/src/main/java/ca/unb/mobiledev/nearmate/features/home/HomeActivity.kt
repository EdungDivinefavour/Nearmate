package ca.unb.mobiledev.nearmate.features.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ca.unb.mobiledev.nearmate.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.get

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(this)

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

        // Sync indicator when swiping
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(index: Int) {
                bottomNav.menu[index].isChecked = true
            }
        })
    }
}