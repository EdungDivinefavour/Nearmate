package ca.unb.mobiledev.nearmate.features.home.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.utils.ImageUtils
import com.bumptech.glide.Glide

class InfoWindowAdapter(
    private val context: Context,
    private val users: List<User>
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.info_window, null)

        val user = users.find { it.id == marker.tag }

        val nameText = view.findViewById<TextView>(R.id.user_name)
        val countryText = view.findViewById<TextView>(R.id.user_country)
        val statusText = view.findViewById<TextView>(R.id.user_status)
        val profileImage = view.findViewById<ImageView>(R.id.user_profile_image)
        val countryFlag = view.findViewById<ImageView>(R.id.user_country_flag)

        user?.let {
            nameText.text = if (it.prefersToShowUserName && !it.userName.isNullOrEmpty()) it.userName else "${it.firstName} ${it.lastName}"
            countryText.text = it.country?.name ?: "Unknown"
            statusText.text = it.status.value

            countryFlag.setImageBitmap(ImageUtils.getCountryBitmap(user.country, context))

            if (!it.profilePhoto.isNullOrEmpty()) {
                Glide.with(context)
                    .load(it.profilePhoto)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .circleCrop()
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.baseline_account_circle_24)
            }
        }

        return view
    }

    override fun getInfoContents(marker: Marker): View? = null
}
