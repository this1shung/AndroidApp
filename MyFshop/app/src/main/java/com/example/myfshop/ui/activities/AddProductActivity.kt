package com.example.myfshop.ui.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.utils.Constants
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.Product
import com.example.myfshop.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity() , View.OnClickListener{

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    private var mProductID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()

        val spinnerCategory = findViewById<Spinner>(R.id.spinner_product_category)


        val ivAddUpdateProduct = findViewById<ImageView>(R.id.iv_add_update_product)
        ivAddUpdateProduct.setOnClickListener(this)

        val btnSubmit = findViewById<Button>(R.id.btn_submit_add_product)
        btnSubmit.setOnClickListener(this)
    }

    private fun setupActionBar() {

        val toolbarAddProductActivity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_product_activity)

        setSupportActionBar(toolbarAddProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when (v?.id) {
                R.id.iv_add_update_product -> {
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
                R.id.btn_submit_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_MEDIA_IMAGES_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProductActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val ivAddUpdateProduct = findViewById<ImageView>(R.id.iv_add_update_product)
        val ivProductImage = findViewById<ImageView>(R.id.iv_product_image)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            ivAddUpdateProduct.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddProductActivity,
                    R.drawable.ic_vector_edit
                )
            )

            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the product image in the ImageView.
                GlideLoader(this@AddProductActivity).loadProductPicture(
                    mSelectedImageFileUri!!,
                    ivProductImage
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateProductDetails(): Boolean {
        val etproducttitle = findViewById<EditText>(R.id.et_product_title)
        val etproductprice = findViewById<EditText>(R.id.et_product_price)
        val etproductdescription = findViewById<EditText>(R.id.et_product_description)
        val etproductquantity = findViewById<EditText>(R.id.et_product_quantity)

        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(etproducttitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(etproductprice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(etproductdescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(etproductquantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.PRODUCT_IMAGE)
    }

    private fun uploadProductDetails() {
        val etproducttitle = findViewById<EditText>(R.id.et_product_title)
        val etproductprice = findViewById<EditText>(R.id.et_product_price)
        val etproductdescription = findViewById<EditText>(R.id.et_product_description)
        val etproductquantity = findViewById<EditText>(R.id.et_product_quantity)
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_product_category)

        val username =
            this.getSharedPreferences(Constants.MYFSHOP_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val selectedCategory = spinnerCategory.selectedItem.toString()

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            etproducttitle .text.toString().trim { it <= ' ' },
            etproductprice.text.toString().trim { it <= ' ' },
            etproductdescription.text.toString().trim { it <= ' ' },
            etproductquantity.text.toString().trim { it <= ' ' },
            mProductImageURL,
            "",
            selectedCategory
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
    }

    fun productUploadSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}