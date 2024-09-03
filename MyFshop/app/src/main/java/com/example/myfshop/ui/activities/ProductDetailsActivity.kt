package com.example.myfshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        //var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        getProductDetails()

        val btn_add_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_go_to_cart)
        if (FirestoreClass().getCurrentUserID() == mProductOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }
    private fun setupActionBar() {

        val toolbarproductdetailsactivity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_product_details_activity)

        setSupportActionBar(toolbarproductdetailsactivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarproductdetailsactivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun productDetailsSuccess(product: Product) {

        val tv_product_details_title = findViewById<com.example.myfshop.utils.MSVTextViewBold>(R.id.tv_product_details_title)
        val tv_product_details_price = findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_price)
        val tv_product_details_description = findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_description)
        val tv_product_details_stock_quantity = findViewById<com.example.myfshop.utils.MSVTextView>(R.id.tv_product_details_stock_quantity)
        val iv_product_detail_image = findViewById<ImageView>(R.id.iv_product_detail_image)
        val btn_add_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart)
        mProductDetails = product
        //hideProgressDialog()

        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity

        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()

            btn_add_to_cart.visibility = View.GONE

            tv_product_details_stock_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }


    }
    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    private fun addToCart() {

        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this@ProductDetailsActivity, cartItem)
    }

    override fun onClick(v: View) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart ->{
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()
        val btn_add_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_go_to_cart)

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        val btn_add_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<com.example.myfshop.utils.MSPButton>(R.id.btn_go_to_cart)
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }
}