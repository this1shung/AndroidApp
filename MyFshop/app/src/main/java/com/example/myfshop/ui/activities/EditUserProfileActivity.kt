
package com.example.myfshop.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.User
import com.example.myfshop.utils.GlideLoader
//import kotlinx.android.synthetic.main.activity_edit_user_profile.*
//import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
import com.example.myfshop.databinding.ActivityEditUserProfileBinding

class EditUserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditUserProfileBinding
    private var userId: String = ""
    private var selectedImageFileUri: Uri? = null
    private var userProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        // Get the user data from intent
        if (intent.hasExtra("user_id")) {
            userId = intent.getStringExtra("user_id")!!
            loadUserDetails(userId)
        }

        // Handle Save Button Click
        binding.btnSubmit.setOnClickListener {
            saveUserDetails()
        }
        binding.ivUserPhoto.setOnClickListener {
            selectImageFromGallery()
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarUserProfileActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun loadUserDetails(userId: String) {
        // Retrieve user details from Firestore and populate fields
        FirestoreClass().getUserDetails(userId)
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                populateUserDetails(user)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateUserDetails(user: User) {
        GlideLoader(this).loadUserPicture(user.image, binding.ivUserPhoto)

        binding.etFirstName.setText(user.firstName)
        binding.etLastName.setText(user.lastName)
        binding.etEmail.setText(user.email)
        binding.etMobileNumber.setText(user.mobile.toString())

        when (user.gender) {
            "Male" -> binding.rbMale.isChecked = true
            "Female" -> binding.rbFemale.isChecked = true
        }

        when (user.role) {
            "user" -> binding.rbUser.isChecked = true
            "admin" -> binding.rbAdmin.isChecked = true
        }
    }

    private fun saveUserDetails() {
        // Get updated data from the fields
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val mobileNumber = binding.etMobileNumber.text.toString().trim().toLong()

        val gender = if (binding.rbMale.isChecked) "Male" else "Female"
        val role = if (binding.rbAdmin.isChecked) "admin" else "user"

        // Create a User object with updated details
        val userHashMap = HashMap<String, Any>()
        userHashMap["firstName"] = firstName
        userHashMap["lastName"] = lastName
        userHashMap["email"] = email
        userHashMap["mobile"] = mobileNumber
        userHashMap["gender"] = gender
        userHashMap["role"] = role

        if (selectedImageFileUri != null) {
            FirestoreClass().uploadImageToCloudStorage(this, selectedImageFileUri!!, "user_profile_image")
        } else {
            updateUserProfile(userHashMap)
        }

        // Update user data in Firestore
        FirestoreClass().updateUserDetails(userId, userHashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User profile updated successfully", Toast.LENGTH_SHORT).show()

                // Return to the UserFragment
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateUserProfile(userHashMap: HashMap<String, Any>) {
        FirestoreClass().updateUserDetails(userId, userHashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User profile updated successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun imageUploadSuccess(imageURL: String) {
        userProfileImageURL = imageURL

        val userHashMap = HashMap<String, Any>()
        userHashMap["image"] = userProfileImageURL

        saveUserDetails()
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageFileUri = uri
                GlideLoader(this).loadUserPicture(selectedImageFileUri!!, binding.ivUserPhoto)
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
