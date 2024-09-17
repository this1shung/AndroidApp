package com.example.myfshop.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfshop.R
import com.example.myfshop.databinding.ActivityEditProductBinding
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.Product
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EditProductActivity : BaseActivity() {

    private var mProductId: String? = null
    private lateinit var mProductDetails: Product
    private lateinit var binding: ActivityEditProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)
            loadProductDetails()
        }

        setupActionBar()
        //setupCategorySpinner()

        binding.ivAddUpdateProduct.setOnClickListener {
            // Logic to upload a new image or change the current one.
        }

        binding.btnSubmit.setOnClickListener {
            if (validateProductDetails()) {
                updateProductDetails()
                //hideProgressDialog()
            }
        }
        setupCategorySpinner("Category")
        //setupCategorySpinner()

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
    private fun loadProductDetails() {
        if (mProductId != null) {
            FirebaseFirestore.getInstance()
                .collection(Constants.PRODUCTS)
                .document(mProductId!!)
                .get()
                .addOnSuccessListener { document: DocumentSnapshot ->
                    mProductDetails = document.toObject(Product::class.java)!!

                    binding.etProductTitle.setText(mProductDetails.title)
                    binding.etProductPrice.setText(mProductDetails.price)
                    binding.etProductDescription.setText(mProductDetails.description)
                    binding.etProductQuantity.setText(mProductDetails.stock_quantity)
                    //binding.spinnerProductCategory.setSelection(mProductDetails.category)
                    GlideLoader(this).loadProductPicture(mProductDetails.image, binding.ivProductImage)

                    setupCategorySpinner(mProductDetails.category)
                }
                .addOnFailureListener { e ->
                    showErrorSnackBar("Failed to load product details.", true)
                }
        }

    }


    private fun validateProductDetails(): Boolean {
        return when {
            binding.etProductTitle.text.toString().trim().isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }
            binding.etProductPrice.text.toString().trim().isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            binding.etProductDescription.text.toString().trim().isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description), true)
                false
            }
            binding.etProductQuantity.text.toString().trim().isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }
            binding.spinnerProductCategory.selectedItem.toString() == "Category" -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun updateProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val productHashMap = HashMap<String, Any>()
        productHashMap[Constants.TITLE] = binding.etProductTitle.text.toString().trim()
        productHashMap[Constants.PRICE] = binding.etProductPrice.text.toString().trim()
        productHashMap[Constants.DESCRIPTION] = binding.etProductDescription.text.toString().trim()
        productHashMap[Constants.STOCK_QUANTITY] = binding.etProductQuantity.text.toString().trim()
        productHashMap[Constants.CATEGORY]= binding.spinnerProductCategory.selectedItem.toString()
        mProductId?.let { id ->
            FirestoreClass().updateProductDetails(this, id, productHashMap)
                .addOnSuccessListener {
                    hideProgressDialog()
                    Toast.makeText(this@EditProductActivity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("isProductUpdated", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                    productUpdateSuccess()
                }
                .addOnFailureListener { e ->
                    hideProgressDialog()
                    Toast.makeText(this@EditProductActivity, "Error while updating product details.", Toast.LENGTH_SHORT).show()
                }
        }
        hideProgressDialog()
    }
    private fun setupCategorySpinner(selectedCategory: String) {
        val categories = resources.getStringArray(R.array.product_category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductCategory.adapter = adapter

        val categoryPosition = adapter.getPosition(selectedCategory)
        binding.spinnerProductCategory.setSelection(categoryPosition)
    }


    fun productUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this@EditProductActivity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
        //hideProgressDialog()

    }

    fun productUpdateFailure() {
        hideProgressDialog()
        Toast.makeText(this@EditProductActivity, "Failed to update product.", Toast.LENGTH_SHORT).show()
    }
}
