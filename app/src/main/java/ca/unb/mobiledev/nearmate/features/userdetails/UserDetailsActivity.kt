package ca.unb.mobiledev.nearmate.features.userdetails

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.chatroom.ChatRoomActivity
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.ChatService
import ca.unb.mobiledev.nearmate.utils.ImageUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var user: User
    private val chatService = ChatService()
    private val currentUserId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        user = intent.getParcelableExtra<User>("user") ?: return

        // Enable action bar with back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE))

        // Ensure content is below the action bar
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.rootLayout)
        rootLayout?.post {
            val tv = android.util.TypedValue()
            if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                val actionBarHeight = android.util.TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
                rootLayout.setPadding(0, actionBarHeight, 0, rootLayout.paddingBottom)
            }
        }

        setupStartChatButton()
        displayUserDetails(user)
        checkChatExists()
    }

    override fun onResume() {
        super.onResume()
        // Refresh button text when returning from chat
        checkChatExists()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupStartChatButton() {
        val startChatButton: Button = findViewById(R.id.startChatButton)
        startChatButton.setOnClickListener {
            val chatId = chatService.getChatId(currentUserId, user.id)
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("chatId", chatId)
            intent.putExtra("otherUserId", user.id)
            intent.putExtra("otherUser", user)
            startActivity(intent)
        }
    }

    private fun checkChatExists() {
        val startChatButton: Button = findViewById(R.id.startChatButton)
        chatService.chatExists(currentUserId, user.id).thenAccept { exists ->
            runOnUiThread {
                startChatButton.text = if (exists) "Message Again" else "Start Chat"
            }
        }
    }

    private fun displayUserDetails(user: User) {
        val profilePhoto: ImageView = findViewById(R.id.profilePhoto)
        val nameText: TextView = findViewById(R.id.nameText)
        val usernameText: TextView = findViewById(R.id.usernameText)
        val countryText: TextView = findViewById(R.id.countryText)
        val statusText: TextView = findViewById(R.id.statusText)
        val presenceText: TextView = findViewById(R.id.presenceText)
        val countryFlag: ImageView = findViewById(R.id.countryFlag)

        // Display name
        val displayName = if (user.prefersToShowUserName && !user.userName.isNullOrEmpty()) {
            user.userName
        } else {
            "${user.firstName} ${user.lastName}"
        }
        nameText.text = displayName

        // Display username if available
        if (!user.userName.isNullOrEmpty() && !user.prefersToShowUserName) {
            usernameText.text = "@${user.userName}"
            usernameText.visibility = android.view.View.VISIBLE
        } else {
            usernameText.visibility = android.view.View.GONE
        }

        // Display country
        countryText.text = user.country?.value ?: "Not specified"

        // Display status
        statusText.text = user.status.value

        // Display presence
        presenceText.text = user.presence.value

        // Load profile photo
        if (!user.profilePhoto.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.profilePhoto)
                .placeholder(R.drawable.baseline_account_circle_24)
                .circleCrop()
                .into(profilePhoto)
        } else {
            profilePhoto.setImageResource(R.drawable.baseline_account_circle_24)
        }

        // Load country flag
        countryFlag.setImageBitmap(ImageUtils.getCountryBitmap(user.country, this))
    }
}

