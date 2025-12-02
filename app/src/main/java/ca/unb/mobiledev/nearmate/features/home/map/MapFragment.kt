package ca.unb.mobiledev.nearmate.features.home.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.LocationService
import ca.unb.mobiledev.nearmate.services.UserService
import ca.unb.mobiledev.nearmate.utils.ImageUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val locationService by lazy { LocationService(requireActivity()) }
    private val userService = UserService()
    private val markers = mutableMapOf<String, Marker>()
    private val pinAnimator = PinAnimator()
    private val users = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val context = context ?: return
        mMap.setInfoWindowAdapter(InfoWindowAdapter(context, users))
        
        mMap.setOnInfoWindowClickListener { marker ->
            // This handles clicks on the info window (including the button area)
            // The button is just visual - clicking anywhere on the info window opens the profile
            val userId = marker.tag as? String
            val user = users.find { it.id == userId }
            user?.let {
                val intent = android.content.Intent(context, ca.unb.mobiledev.nearmate.features.userdetails.UserDetailsActivity::class.java)
                intent.putExtra("user", it)
                context.startActivity(intent)
            }
        }

        locationService.getLocation { location ->
            if (location == null) {
                locationService.requestLocationPermission()
            }

            else {
                userService.listenForUsersNearLocation(location.latitude, location.longitude, MAP_FETCH_RADIUS) { nearbyUsers ->
                    if (!isAdded || context == null) return@listenForUsersNearLocation
                    
                    users.clear()
                    users.addAll(nearbyUsers)

                    updateMarkers(users)

                    if (isAdded && ::mMap.isInitialized) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
                        pinAnimator.startRadarPulse(location, mMap)
                    }
                }

            }
        }
    }

    private fun updateMarkers(users: List<User>) {
        val context = context ?: return // Check if fragment is attached
        if (!isAdded) return // Double check fragment is attached
        
        val existingIds = markers.keys.toMutableSet()

        users.forEach { user ->
            val position = LatLng(user.lat, user.lng)
            val icon = ImageUtils.getCountryFlagBitmapDescriptor(user.country, context)

            if (markers.containsKey(user.id)) {
                markers[user.id]?.position = position
                markers[user.id]?.setIcon(icon)
            } else {
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .icon(icon) // set the flag icon here
                )
                marker?.tag = user.id
                if (marker != null) markers[user.id] = marker
            }
            existingIds.remove(user.id)
        }

        existingIds.forEach { id ->
            markers[id]?.remove()
            markers.remove(id)
        }

        if (users.isNotEmpty() && markers.size == users.size) {
            val first = users.first()
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.lat, first.lng), 10f))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        pinAnimator.dispose()
        userService.stopListening()
    }

    companion object {
        private const val ZOOM_LEVEL = 16.5f
        private const val MAP_FETCH_RADIUS = 50.0
    }
}
