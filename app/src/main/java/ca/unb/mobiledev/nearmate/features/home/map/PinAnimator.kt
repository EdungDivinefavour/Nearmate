package ca.unb.mobiledev.nearmate.features.home.map

import android.animation.ValueAnimator
import android.graphics.Color
import ca.unb.mobiledev.nearmate.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

class PinAnimator {
    private var centerCircle: Circle? = null
    private var pulseCircle: Circle? = null
    private var pulseAnimator: ValueAnimator? = null

    fun startRadarPulse(userLatLng: LatLng, map: GoogleMap) {
        // Remove previous circles
        centerCircle?.remove()
        pulseCircle?.remove()
        pulseAnimator?.cancel()

        // Draws a small center dot as a circle
        centerCircle = map.addCircle(
            CircleOptions()
                .center(userLatLng)
                .radius(USER_DOT_RADIUS) // small radius in meters
                .strokeColor(R.color.colorPrimaryDark)
                .strokeWidth(2f)
                .fillColor(R.color.colorPrimary)
        )

        // Pulsing light blue circle around the center
        pulseCircle = map.addCircle(
            CircleOptions()
                .center(userLatLng)
                .radius(USER_DOT_RADIUS)
                .strokeColor(R.color.colorPrimaryDark)
                .strokeWidth(0.5f)
                .fillColor(R.color.colorPrimaryLight)
        )

        pulseAnimator = ValueAnimator.ofFloat(USER_DOT_RADIUS.toFloat(), USER_DOT_MAX_RADIUS.toFloat()).apply {
            duration = 1000L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation ->
                val animatedRadius = (animation.animatedValue as Float).toDouble()
                pulseCircle?.radius = animatedRadius

                val alpha = ((1 - (animatedRadius - USER_DOT_RADIUS) / USER_DOT_MAX_RADIUS) * 0x22).toInt().coerceIn(0, 0x22)
                pulseCircle?.fillColor = Color.argb(alpha, 0, 0, 255)
            }

            start()
        }
    }

    fun dispose() {
        pulseAnimator?.cancel()
        pulseCircle?.remove()
        centerCircle?.remove()
    }

    companion object {
        private const val USER_DOT_RADIUS = 4.0
        private const val USER_DOT_MAX_RADIUS = 40.0
    }
}