package com.example.myfshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.User
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()
        val tvEdit = findViewById<TextView>(R.id.tv_edit)
        tvEdit.setOnClickListener(this)
        val btnlogout = findViewById<Button>(R.id.btn_logout)
        btnlogout.setOnClickListener(this)
        val llAddress = findViewById<LinearLayout>(R.id.ll_address)
        llAddress.setOnClickListener(this)

    }
    private fun setupActionBar() {

        val toolbarSettingActivity = findViewById<Toolbar>(R.id.toolbar_settings_activity)
        setSupportActionBar(toolbarSettingActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarSettingActivity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvGender = findViewById<TextView>(R.id.tv_gender)
        val tvEmail = findViewById<TextView>(R.id.tv_email)
        val tvMobileNumber = findViewById<TextView>(R.id.tv_mobile_number)
        val ivUserPhoto = findViewById<ImageView>(R.id.iv_user_photo)
        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, ivUserPhoto)

        tvName.text = "${user.firstName} ${user.lastName}"
        tvGender.text = user.gender
        tvEmail.text = user.email
        tvMobileNumber.text = "${user.mobile}"
        // END
    }

    override fun onResume() {
        super.onResume()

        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}