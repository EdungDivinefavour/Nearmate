package ca.unb.mobiledev.nearmate.features.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.unb.mobiledev.nearmate.features.home.users.UserListFragment
import ca.unb.mobiledev.nearmate.features.home.map.MapFragment
import ca.unb.mobiledev.nearmate.features.home.profile.ProfileFragment
import ca.unb.mobiledev.nearmate.features.home.chats.ChatListFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        UserListFragment(),
        MapFragment(),
        ChatListFragment(),
        ProfileFragment(),
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
