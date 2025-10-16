package ca.unb.mobiledev.nearmate.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class DistanceUtils {
    companion object {
        fun latDelta(radius: Double): Double {
            return radius / 111.0
        }

        fun lngDelta(lat: Double, radius: Double): Double {
            return radius / (111.0 * cos(Math.toRadians(lat)))
        }

        fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2.0) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return R * c
        }
    }

}