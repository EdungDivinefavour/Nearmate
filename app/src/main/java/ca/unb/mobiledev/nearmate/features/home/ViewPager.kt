package ca.unb.mobiledev.nearmate.features.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.unb.mobiledev.nearmate.features.home.tabs.ListFragment
import ca.unb.mobiledev.nearmate.features.home.tabs.MapFragment
import ca.unb.mobiledev.nearmate.features.home.tabs.ProfileFragment
import ca.unb.mobiledev.nearmate.features.home.tabs.SettingsFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        ListFragment(),
        MapFragment(),
        ProfileFragment(),
        SettingsFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
