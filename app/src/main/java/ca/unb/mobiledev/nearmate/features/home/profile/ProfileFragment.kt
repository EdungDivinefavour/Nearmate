package ca.unb.mobiledev.nearmate.features.home.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.constants.Presence
import ca.unb.mobiledev.nearmate.constants.Status
import ca.unb.mobiledev.nearmate.features.landing.LandingActivity
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.UserService
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private lateinit var profilePhoto: ImageView
    private lateinit var editPhotoButton: ImageButton
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var preferUsernameCheckbox: CheckBox
    private lateinit var statusSpinner: Spinner
    private lateinit var presenceToggle: Switch
    private lateinit var presenceToggleLabel: TextView
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button
    private lateinit var saveProgressBar: ProgressBar

    private val userService = UserService()
    private var currentUser: User? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                uploadProfilePhoto(imageUri)
            } else if (cameraImageUri != null) {
                // Camera result
                uploadProfilePhoto(cameraImageUri!!)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    private var cameraImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePhoto = view.findViewById(R.id.profilePhoto)
        editPhotoButton = view.findViewById(R.id.editPhotoButton)
        firstNameInput = view.findViewById(R.id.firstNameInput)
        lastNameInput = view.findViewById(R.id.lastNameInput)
        usernameInput = view.findViewById(R.id.usernameInput)
        preferUsernameCheckbox = view.findViewById(R.id.preferUsernameCheckbox)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        presenceToggle = view.findViewById(R.id.presenceToggle)
        presenceToggleLabel = view.findViewById(R.id.presenceToggleLabel)
        saveButton = view.findViewById(R.id.saveButton)
        
        // Update label when toggle changes
        presenceToggle.setOnCheckedChangeListener { _, isChecked ->
            presenceToggleLabel.text = if (isChecked) getString(R.string.online) else getString(R.string.offline)
        }
        logoutButton = view.findViewById(R.id.logoutButton)
        saveProgressBar = view.findViewById(R.id.saveProgressBar)

        setupStatusSpinner()
        loadUserProfile()

        saveButton.setOnClickListener {
            saveProfile()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        editPhotoButton.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun setupStatusSpinner() {
        val statuses = Status.values().map { it.value }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = adapter
    }

    private fun loadUserProfile() {
        userService.getCurrentUser()
            .thenAccept { user ->
                currentUser = user
                if (user != null) {
                    activity?.runOnUiThread {
                        displayUser(user)
                    }
                }
            }
            .exceptionally { e ->
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                null
            }
    }

    private fun displayUser(user: User) {
        firstNameInput.setText(user.firstName)
        lastNameInput.setText(user.lastName)
        usernameInput.setText(user.userName ?: "")
        preferUsernameCheckbox.isChecked = user.prefersToShowUserName

        // Set status spinner
        val statusIndex = Status.values().indexOfFirst { it == user.status }
        if (statusIndex >= 0) {
            statusSpinner.setSelection(statusIndex)
        }

        // Set presence toggle
        presenceToggle.isChecked = user.presence == Presence.ONLINE
        presenceToggleLabel.text = if (user.presence == Presence.ONLINE) getString(R.string.online) else getString(R.string.offline)

        // Load profile photo
        loadProfilePhoto(user.profilePhoto)
    }

    private fun loadProfilePhoto(photoUrl: String?) {
        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.baseline_account_circle_24)
                .circleCrop()
                .into(profilePhoto)
        } else {
            profilePhoto.setImageResource(R.drawable.baseline_account_circle_24)
        }
    }

    private fun saveProfile() {
        val user = currentUser ?: return

        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        val prefersUsername = preferUsernameCheckbox.isChecked
        val selectedStatus = Status.values()[statusSpinner.selectedItemPosition]
        val presence = if (presenceToggle.isChecked) Presence.ONLINE else Presence.OFFLINE

        if (firstName.isEmpty()) {
            firstNameInput.error = "First name is required"
            return
        }

        if (lastName.isEmpty()) {
            lastNameInput.error = "Last name is required"
            return
        }

        val updatedUser = user.copy(
            firstName = firstName,
            lastName = lastName,
            userName = if (username.isNotEmpty()) username else null,
            prefersToShowUserName = prefersUsername,
            status = selectedStatus,
            presence = presence
        )

        saveButton.text = ""
        saveButton.isEnabled = false
        saveProgressBar.visibility = View.VISIBLE

        userService.updateProfile(updatedUser)
            .thenAccept { savedUser ->
                currentUser = savedUser
                activity?.runOnUiThread {
                    saveProgressBar.visibility = View.GONE
                    saveButton.text = getString(R.string.save)
                    saveButton.isEnabled = true
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .exceptionally { e ->
                activity?.runOnUiThread {
                    saveProgressBar.visibility = View.GONE
                    saveButton.text = getString(R.string.save)
                    saveButton.isEnabled = true
                    Toast.makeText(requireContext(), "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                null
            }
    }

    private fun logout() {
        userService.logout()
            .thenAccept { success ->
                if (success) {
                    activity?.runOnUiThread {
                        val intent = Intent(requireContext(), LandingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
            .exceptionally { e ->
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error logging out: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                null
            }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf(getString(R.string.camera), getString(R.string.gallery))
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_image_source))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val context = context ?: return
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.CAMERA
        } else {
            Manifest.permission.CAMERA
        }
        
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(permission)
            }
        }
    }

    private fun launchCamera() {
        val photoFile = java.io.File(requireContext().cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        
        imagePickerLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun uploadProfilePhoto(imageUri: Uri) {
        editPhotoButton.isEnabled = false
        saveProgressBar.visibility = View.VISIBLE

        userService.uploadProfilePhoto(imageUri)
            .thenAccept { photoUrl ->
                val user = currentUser ?: return@thenAccept
                val updatedUser = user.copy(profilePhoto = photoUrl)
                
                userService.updateProfile(updatedUser)
                    .thenAccept { savedUser ->
                        currentUser = savedUser
                        activity?.runOnUiThread {
                            saveProgressBar.visibility = View.GONE
                            editPhotoButton.isEnabled = true
                            
                            // Update the image view
                            loadProfilePhoto(photoUrl)
                            
                            Toast.makeText(requireContext(), "Profile photo updated successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .exceptionally { e ->
                        activity?.runOnUiThread {
                            saveProgressBar.visibility = View.GONE
                            editPhotoButton.isEnabled = true
                            Toast.makeText(requireContext(), "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        null
                    }
            }
            .exceptionally { e ->
                activity?.runOnUiThread {
                    saveProgressBar.visibility = View.GONE
                    editPhotoButton.isEnabled = true
                    Toast.makeText(requireContext(), "Error uploading photo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                null
            }
    }
}
