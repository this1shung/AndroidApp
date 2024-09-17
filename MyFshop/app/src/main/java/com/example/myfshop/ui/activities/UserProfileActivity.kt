package com.example.myfshop.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.utils.Constants
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.User
import com.example.myfshop.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private var mUserDetails: User? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mUserDetails = intent.getSerializableExtra(Constants.EXTRA_USER_DETAILS) as? User

        val etFirstName = findViewById<EditText>(R.id.et_first_name)
        val etLastName = findViewById<EditText>(R.id.et_last_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val etMobileNumber = findViewById<EditText>(R.id.et_mobile_number)
        val rbMale = findViewById<RadioButton>(R.id.rb_male)
        val rbFemale = findViewById<RadioButton>(R.id.rb_female)
        val ivUserPhoto = findViewById<ImageView>(R.id.iv_user_photo)



        if (mUserDetails?.profileCompleted == 0) {
            tvTitle.text = resources.getString(R.string.title_complete_profile)
            etFirstName.setText(mUserDetails?.firstName)
            etFirstName.isEnabled = false
            etLastName.setText(mUserDetails?.lastName)
            etLastName.isEnabled = false
            etEmail.setText(mUserDetails?.email)
            etEmail.isEnabled = false
        } else {
            setupActionBar()

            tvTitle.text = resources.getString(R.string.title_edit_profile)

            mUserDetails?.let { GlideLoader(this@UserProfileActivity).loadUserPicture(it.image, ivUserPhoto) }

            etFirstName.setText(mUserDetails?.firstName)
            etLastName.setText(mUserDetails?.lastName)

            etEmail.isEnabled = false
            etEmail.setText(mUserDetails?.email)

            if (mUserDetails?.mobile != 0L) {
                etMobileNumber.setText(mUserDetails?.mobile.toString())
            }
            if (mUserDetails?.gender == Constants.MALE) {
                rbMale.isChecked = true
            } else {
                rbFemale.isChecked = true
            }
        }

        ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        findViewById<View>(R.id.btn_submit).setOnClickListener(this@UserProfileActivity)
    }

    private fun setupActionBar() {

        val toolbarUserProfileActivity = findViewById<Toolbar>(R.id.toolbar_user_profile_activity)

        setSupportActionBar(toolbarUserProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarUserProfileActivity .setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_user_photo -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(this)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        Constants.READ_MEDIA_IMAGES_PERMISSION_CODE
                    )
                }
            }
            R.id.btn_submit -> {
                if (validateUserProfileDetails()) {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    if (mSelectedImageFileUri != null) {
                        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                    } else {
                        updateUserProfileDetails()
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val etMobileNumber = findViewById<EditText>(R.id.et_mobile_number)
        val rbMale = findViewById<RadioButton>(R.id.rb_male)
        val etFirstName = findViewById<EditText>(R.id.et_first_name)
        val etLastName = findViewById<EditText>(R.id.et_last_name)
        val userHashMap = HashMap<String, Any>()

        val firstName = etFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails?.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails?.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = etMobileNumber.text.toString().trim()

        val gender = if (rbMale.isChecked) Constants.MALE else Constants.FEMALE

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        } else {
            userHashMap[Constants.MOBILE] = 0L
        }
        if (gender.isNotEmpty() && gender != mUserDetails?.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_MEDIA_IMAGES_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            data?.data?.let { uri ->
                try {
                    mSelectedImageFileUri = uri
                    val ivUserPhoto = findViewById<ImageView>(R.id.iv_user_photo)
                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, ivUserPhoto)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, resources.getString(R.string.image_selection_failed), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        val etMobileNumber = findViewById<EditText>(R.id.et_mobile_number)
        return if (TextUtils.isEmpty(etMobileNumber.text.toString().trim())) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
            false
        } else {
            true
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.msg_profile_update_success), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}
