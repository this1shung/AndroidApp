package com.example.myfshop.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.Address
import com.example.myfshop.utils.Constants
import com.google.android.material.textfield.TextInputLayout

class AddEditAddressActivity : BaseActivity() {
    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit_address)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = (intent.getSerializableExtra(Constants.EXTRA_ADDRESS_DETAILS) as Address?)!!
        }

        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val btnSubmitAddress = findViewById<Button>(R.id.btn_submit_address)
        val etFullName = findViewById<EditText>(R.id.et_full_name)
        val etPhoneNumber = findViewById<EditText>(R.id.et_phone_number)
        val etAddress = findViewById<EditText>(R.id.et_address)
        val etZipCode = findViewById<EditText>(R.id.et_zip_code)
        val etAdditionalNote = findViewById<EditText>(R.id.et_additional_note)
        val rbHome = findViewById<RadioButton>(R.id.rb_home)
        val rbOffice = findViewById<RadioButton>(R.id.rb_office)
        val rbOther = findViewById<RadioButton>(R.id.rb_other)
        val tilOtherDetails = findViewById<TextInputLayout>(R.id.til_other_details)
        val etOtherDetails = tilOtherDetails.editText

        tvTitle.text = resources.getString(R.string.title_edit_address)
        btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)

        etFullName?.setText(mAddressDetails?.name)
        etPhoneNumber?.setText(mAddressDetails?.mobileNumber)
        etAddress?.setText(mAddressDetails?.address)
        etZipCode?.setText(mAddressDetails?.zipCode)
        etAdditionalNote?.setText(mAddressDetails?.additionalNote)

        when (mAddressDetails?.type) {
            Constants.HOME -> rbHome.isChecked = true
            Constants.OFFICE -> rbOffice.isChecked = true
            else -> {
                rbOther.isChecked = true
                tilOtherDetails.visibility = View.VISIBLE
                etOtherDetails?.setText(mAddressDetails?.otherDetails)
            }
        }

        setupActionBar()

        btnSubmitAddress.setOnClickListener {
            saveAddressToFirestore()
        }

        val rgType = findViewById<RadioGroup>(R.id.rg_type)
        rgType.setOnCheckedChangeListener { _, checkedId ->
            tilOtherDetails.visibility = if (checkedId == R.id.rb_other) View.VISIBLE else View.GONE
        }
    }

    private fun setupActionBar() {
        val toolbar_add_edit_address_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_edit_address_activity)

        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun validateData(): Boolean {
        val etFullName = findViewById<EditText>(R.id.et_full_name)
        val etPhoneNumber = findViewById<EditText>(R.id.et_phone_number)
        val etAddress = findViewById<EditText>(R.id.et_address)
        val etZipCode = findViewById<EditText>(R.id.et_zip_code)
        val rbOther = findViewById<RadioButton>(R.id.rb_other)
        val etOtherDetails = findViewById<EditText>(R.id.et_other_details)
        return when {
            TextUtils.isEmpty(etFullName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name), true)
                false
            }
            TextUtils.isEmpty(etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number), true)
                false
            }
            TextUtils.isEmpty(etAddress.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            rbOther.isChecked && TextUtils.isEmpty(etOtherDetails?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_other_details), true)
                false
            }
            else -> true
        }
    }

    private fun saveAddressToFirestore() {
        val fullName = findViewById<EditText>(R.id.et_full_name).text.toString().trim { it <= ' ' }
        val phoneNumber = findViewById<EditText>(R.id.et_phone_number).text.toString().trim { it <= ' ' }
        val address = findViewById<EditText>(R.id.et_address).text.toString().trim { it <= ' ' }
        val zipCode = findViewById<EditText>(R.id.et_zip_code).text.toString().trim { it <= ' ' }
        val additionalNote = findViewById<EditText>(R.id.et_additional_note).text.toString().trim { it <= ' ' }
        val otherDetails = findViewById<TextInputLayout>(R.id.til_other_details).editText?.text.toString().trim { it <= ' ' }

        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType = when {
                findViewById<RadioButton>(R.id.rb_home).isChecked -> Constants.HOME
                findViewById<RadioButton>(R.id.rb_office).isChecked -> Constants.OFFICE
                else -> Constants.OTHER
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(this, addressModel, mAddressDetails!!.id)
            } else {
                FirestoreClass().addAddress(this, addressModel)
            }
        }
    }


    fun addUpdateAddressSuccess() {

        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }
        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }

}