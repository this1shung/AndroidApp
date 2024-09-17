package com.example.myfshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.CartItem
import com.example.myfshop.models.Product
import com.example.myfshop.models.Rating
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""

    private lateinit var ratingBar: RatingBar
    private lateinit var tvAverageRating: TextView
    private var savedRatingBarRating: Float = 0f

    private lateinit var spinnerProductSize: Spinner
    private var selectedSize: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        ratingBar = findViewById(R.id.rating_bar)
        tvAverageRating = findViewById(R.id.tv_average_rating)
        spinnerProductSize = findViewById(R.id.spinner_product_size)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        setupButtons()
        setupRatingBar()
        setupSpinner()

        getProductDetails()

        if (savedInstanceState != null) {
            savedRatingBarRating = savedInstanceState.getFloat("savedRatingBarRating")
            ratingBar.rating = savedRatingBarRating
        }
    }

    private fun setupSpinner() {
        spinnerProductSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSize = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("savedRatingBarRating", ratingBar.rating)
    }

    private fun setupActionBar() {
        val toolbarProductDetailsActivity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_product_details_activity)
        setSupportActionBar(toolbarProductDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupButtons() {
        val btnAddToCart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart)
        val btnGoToCart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_go_to_cart)

        if (FirestoreClass().getCurrentUserID() == mProductOwnerId) {
            btnAddToCart.visibility = View.GONE
            btnGoToCart.visibility = View.GONE
        } else {
            btnAddToCart.visibility = View.VISIBLE
        }

        btnAddToCart.setOnClickListener(this)
        btnGoToCart.setOnClickListener(this)
    }

    private fun setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                submitRating(rating)
            }
        }
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product

        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            findViewById(R.id.iv_product_detail_image)
        )
        findViewById<com.example.myfshop.utils.MSVTextViewBold>(R.id.tv_product_details_title).text = product.title
        findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_price).text = "$${product.price}"
        findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_description).text = product.description
        findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_stock_quantity).text = product.stock_quantity

        ratingBar.rating = product.average_rating
        tvAverageRating.text = String.format("Average Rating: %.1f", product.average_rating)

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart).visibility = View.GONE
            findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_stock_quantity).apply {
                text = resources.getString(R.string.lbl_out_of_stock)
                setTextColor(ContextCompat.getColor(this@ProductDetailsActivity, R.color.colorSnackBarError))
            }
        } else {
            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
        loadUserRating()
    }

    private fun loadUserRating() {
        FirestoreClass().getUserRatingForProduct(mProductId, FirestoreClass().getCurrentUserID()) { rating ->
            if (rating != null) {
                ratingBar.rating = rating.rating
                ratingBar.isEnabled = false
            }
        }
    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            size = selectedSize
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this@ProductDetailsActivity, cartItem)
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(this@ProductDetailsActivity, resources.getString(R.string.success_message_item_added_to_cart), Toast.LENGTH_SHORT).show()
        findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart).visibility = View.GONE
        findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_go_to_cart).visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        hideProgressDialog()
        findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart).visibility = View.GONE
        findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_go_to_cart).visibility = View.VISIBLE
    }

    private fun submitRating(ratingValue: Float) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val userId = FirestoreClass().getCurrentUserID()
        val rating = Rating(mProductId, userId, ratingValue)
        FirestoreClass().submitRating(rating) { success ->
            hideProgressDialog()
            if (success) {
                Toast.makeText(this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show()
                ratingBar.isEnabled = false
                updateProductAverageRating()
            } else {
                Toast.makeText(this, "Failed to submit rating.", Toast.LENGTH_SHORT).show()
                ratingBar.rating = 0f
            }
        }
    }

    private fun updateProductAverageRating() {
        FirestoreClass().getProductAverageRating(mProductId) { averageRating ->
            mProductDetails.average_rating = averageRating
            tvAverageRating.text = String.format("Average Rating: %.1f", averageRating)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_add_to_cart -> addToCart()
            R.id.btn_go_to_cart -> startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
        }
    }
}