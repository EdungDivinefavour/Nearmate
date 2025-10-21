package ca.unb.mobiledev.nearmate.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.constants.Country
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.createBitmap

class ImageUtils {
    companion object {
        fun getCountryBitmap(country: Country?, context: Context): Bitmap {
            return drawableToBitmap(getCountryDrawable(country), context)
        }

        fun getCountryFlagBitmapDescriptor(country: Country?, context: Context): BitmapDescriptor {
            val bitmap = drawableToBitmap(getCountryDrawable(country), context)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        private fun drawableToBitmap(drawableRes: Int, context: Context): Bitmap {
            val drawable = AppCompatResources.getDrawable(context, drawableRes)
                ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        private fun getCountryDrawable(country: Country?): Int {
            return when (country) {
                Country.NIGERIA -> R.drawable.nigeria
                Country.CANADA -> R.drawable.canada
                Country.GHANA -> R.drawable.ghana
                Country.INDIA -> R.drawable.india
                else -> R.drawable.baseline_account_circle_24
            }
        }
    }

}