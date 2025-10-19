package ca.unb.mobiledev.nearmate.models

import ca.unb.mobiledev.nearmate.constants.Country
import ca.unb.mobiledev.nearmate.constants.Presence
import ca.unb.mobiledev.nearmate.constants.Status

data class User (
    val id: String,
    val firstName: String,
    val lastName: String,
    val userName: String?,
    val prefersToShowUserName: Boolean,
    val email: String,
    val country: Country?,
    val status: Status,
    val presence: Presence,
    val profilePhoto: String?,
    val lat: Double,
    val lng: Double
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "lastName" to lastName,
            "userName" to userName,
            "prefersToShowUserName" to prefersToShowUserName,
            "email" to email,
            "country" to country?.value,
            "status" to status.value,
            "presence" to presence.value,
            "profilePhoto" to profilePhoto,
            "lat" to lat,
            "lng" to lng
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                id = map["id"] as? String ?: "",
                firstName = map["firstName"] as? String ?: "",
                lastName = map["lastName"] as? String ?: "",
                userName = map["userName"] as? String,
                prefersToShowUserName = map["prefersToShowUserName"] as? Boolean ?: false,
                email = map["email"] as? String ?: "",
                country = (map["country"] as? String)?.let { Country.entries.first { c -> c.value == it } },
                status = Status.entries.first { it.value == map["status"] as? String },
                presence = Presence.entries.first { it.value == map["presence"] as? String },
                profilePhoto = map["profilePhoto"] as? String,
                lat = map["lat"] as? Double ?: 0.0,
                lng = map["lng"] as? Double ?: 0.0
            )
        }
    }
}