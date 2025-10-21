package ca.unb.mobiledev.nearmate.features.home.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.LocationService
import ca.unb.mobiledev.nearmate.services.UserService

class UserListFragment : Fragment() {
    private val userService = UserService()
    private val locationService by lazy { LocationService(requireActivity()) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListAdapter
    private val users = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.userListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserListAdapter(users)
        recyclerView.adapter = adapter

        locationService.getLocation { location ->
            if (location == null) {
                locationService.requestLocationPermission()
            }

            else {
                userService.listenForUsersNearLocation(location.latitude, location.longitude, FETCH_RADIUS) { nearbyUsers ->
                    users.clear()
                    users.addAll(nearbyUsers)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        private const val FETCH_RADIUS = 10.0
    }
}
