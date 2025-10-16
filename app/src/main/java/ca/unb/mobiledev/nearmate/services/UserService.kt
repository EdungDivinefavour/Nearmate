package ca.unb.mobiledev.nearmate.services

import ca.unb.mobiledev.nearmate.constants.Presence
import ca.unb.mobiledev.nearmate.constants.Status
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.utils.DistanceUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

interface UserService {
    fun findNearLocation(lat: Double, lng: Double, radius: Double): CompletableFuture<List<User>>
    fun register(firstName: String, lastName: String, email: String, password: String, lat: Double, lng: Double): CompletableFuture<User?>
    fun login(email: String, password: String): CompletableFuture<User?>
    fun updateProfile(user: User): CompletableFuture<User>
    fun sendPasswordResetEmail(email: String): CompletableFuture<Boolean>
}

class UserServiceImpl: UserService {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseFirestore = FirebaseFirestore.getInstance()

    override fun findNearLocation(lat: Double, lng: Double, radius: Double): CompletableFuture<List<User>> {
        val future = CompletableFuture<List<User>>()

        val minLat = lat - DistanceUtils.latDelta(radius)
        val maxLat = lat + DistanceUtils.latDelta(radius)
        val minLng = lng - DistanceUtils.lngDelta(lng, radius)
        val maxLng = lng + DistanceUtils.lngDelta(lng, radius)

        firebaseFirestore.collection("users")
            .whereGreaterThanOrEqualTo("lat", minLat)
            .whereLessThanOrEqualTo("lat", maxLat)
            .whereGreaterThanOrEqualTo("lng", minLng)
            .whereLessThanOrEqualTo("lng", maxLng)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val nearbyUsers = querySnapshot.documents.mapNotNull { doc ->
                    val map = doc.data ?: return@mapNotNull null
                    val user = User.fromMap(map)

                    val distance = DistanceUtils.haversineDistance(lat, lng, user.lat, user.lng)
                    if (distance <= radius) { user } else null
                }
                future.complete(nearbyUsers)
            }
            .addOnFailureListener { e ->
                future.completeExceptionally(e)
            }

        return future
    }

    override fun register( firstName: String, lastName: String, email: String, password: String, lat: Double, lng: Double ): CompletableFuture<User?> {
        val future = CompletableFuture<User?>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser == null) {
                    future.complete(null)
                    return@addOnSuccessListener
                }

                val newUser =  User(
                    id = firebaseUser.uid,
                    firstName = firstName,
                    lastName = lastName,
                    userName = null,
                    prefersToShowUserName = false,
                    email = email,
                    country = null,
                    status = Status.JUST_HANGING_OUT,
                    presence = Presence.ONLINE,
                    profilePhoto = null,
                    lat = lat,
                    lng = lng
                )

                firebaseFirestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(newUser.toMap())
                    .addOnSuccessListener {
                        future.complete(newUser)
                    }
                    .addOnFailureListener { e ->
                        future.completeExceptionally(e)
                    }
            }
            .addOnFailureListener { e ->
                future.completeExceptionally(e)
            }

        return future
    }

    override fun login(email: String, password: String): CompletableFuture<User?> {
        val future = CompletableFuture<User?>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser == null) {
                    future.complete(null)
                    return@addOnSuccessListener
                }

                firebaseFirestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val map = doc.data
                        val user = if (map != null) User.fromMap(map) else null
                        future.complete(user)
                    }
                    .addOnFailureListener { e ->
                        future.completeExceptionally(e)
                    }
            }
            .addOnFailureListener { e ->
                future.completeExceptionally(e)
            }

        return future
    }

    override fun updateProfile(user: User): CompletableFuture<User> {
        val future = CompletableFuture<User>()

        firebaseFirestore.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener { future.complete(user) }
            .addOnFailureListener { e -> future.completeExceptionally(e) }

        return future
    }

    override fun sendPasswordResetEmail(email: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { e -> future.completeExceptionally(e) }

        return future
    }
}