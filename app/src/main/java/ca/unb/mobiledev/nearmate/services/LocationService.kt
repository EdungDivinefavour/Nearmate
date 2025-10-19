package ca.unb.mobiledev.nearmate.services

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

interface ILocationService {
    fun requestLocationPermission(): Boolean
    fun getLat(): Double
    fun getLng(): Double
    fun startLocationUpdates(onResult: (lat: Double, lng: Double) -> Unit)
    fun stopLocationUpdates()
}

class LocationService(private val activity: Activity) : ILocationService {
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private val handler = Handler(Looper.getMainLooper())
    private var locationRunnable: Runnable? = null

    override fun requestLocationPermission(): Boolean {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        return if (ContextCompat.checkSelfPermission(activity, fineLocationPermission) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(fineLocationPermission), 1001)
            false
        }
    }

    override fun getLat(): Double = currentLat
    override fun getLng(): Double = currentLng

    override fun startLocationUpdates(onResult: (lat: Double, lng: Double) -> Unit) {
        locationRunnable = object : Runnable {
            override fun run() {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            currentLat = location.latitude
                            currentLng = location.longitude
                            onResult(currentLat, currentLng)
                        }
                    }
                }
                handler.postDelayed(this, 5000)
            }
        }
        handler.post(locationRunnable!!)
    }

    override fun stopLocationUpdates() {
        locationRunnable?.let { handler.removeCallbacks(it) }
    }
}
