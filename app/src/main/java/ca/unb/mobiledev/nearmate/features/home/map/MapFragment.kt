package ca.unb.mobiledev.nearmate.features.home.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.LocationService
import ca.unb.mobiledev.nearmate.services.UserService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {
    private val locationService = LocationService.getInstance()
    private lateinit var mMap: GoogleMap
    private val userService = UserService()
    private val markers = mutableMapOf<String, Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        this@MapFragment.mMap = googleMap
        mMap.setInfoWindowAdapter(InfoWindowAdapter(requireContext()))

        locationService.getLocation { location ->
            if (location != null) {
                addMarkerAtCurrentLocation(location)
                userService.listenForUsersNearLocation(location.latitude, location.longitude, MAP_FETCH_RADIUS)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userService.nearbyUsers.collect { users ->
                updateMarkers(users)
            }
        }
    }

    private fun addMarkerAtCurrentLocation(location: LatLng) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
        } else {
            locationService.requestLocationPermission()
        }
    }

    private fun updateMarkers(users: List<User>) {
        val existingIds = markers.keys.toMutableSet()

        users.forEach { user ->
            val position = LatLng(user.lat, user.lng)
            if (markers.containsKey(user.id)) {
                markers[user.id]?.position = position
            } else {
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title("${user.firstName} ${user.lastName}")
                        .snippet("${user.country} - ${user.status}")
                )
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
        userService.stopListening()
    }

    companion object {
        private const val ZOOM_LEVEL = 18.5f
        private const val MAP_FETCH_RADIUS = 10.0
    }
}