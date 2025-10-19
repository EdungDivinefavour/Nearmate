package ca.unb.mobiledev.nearmate.features.home.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import ca.unb.mobiledev.nearmate.R

class InfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.info_window, null)

        val nameText = view.findViewById<TextView>(R.id.user_name)
        val countryText = view.findViewById<TextView>(R.id.user_country)
        val statusText = view.findViewById<TextView>(R.id.user_status)

        nameText.text = marker.title
        marker.snippet?.let {
            val parts = it.split(" - ")
            if (parts.size >= 2) {
                countryText.text = parts[0]
                statusText.text = parts[1]
            }
        }

        return view
    }
}
