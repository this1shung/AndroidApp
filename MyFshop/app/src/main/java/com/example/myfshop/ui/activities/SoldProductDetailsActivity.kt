package com.example.myfshop.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.models.SoldProduct
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SoldProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sold_product_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var productDetails: SoldProduct? = null

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails = intent.getSerializableExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS) as? SoldProduct
        }
        setupActionBar()
        if (productDetails != null) {
            setupUI(productDetails)
        } else {
            // Handle the case where productDetails is null (e.g., show an error message or default content)
            Toast.makeText(this, "Product details not found", Toast.LENGTH_SHORT).show()
            // You might also want to handle navigation or display a default UI state
        }

    }

    private fun setupActionBar() {
        val toolbar_sold_product_details_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sold_product_details_activity)

        setSupportActionBar(toolbar_sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun setupUI(productDetails: SoldProduct) {
        val tvSoldProductDetailsId = findViewById<TextView>(R.id.tv_sold_product_details_id)
        val tvSoldProductDetailsDate = findViewById<TextView>(R.id.tv_sold_product_details_date)
        val ivProductItemImage = findViewById<ImageView>(R.id.iv_product_item_image)
        val tvProductItemName = findViewById<TextView>(R.id.tv_product_item_name)
        val tvProductItemPrice = findViewById<TextView>(R.id.tv_product_item_price)
        val tvSoldProductQuantity = findViewById<TextView>(R.id.tv_sold_product_quantity)
        val tvSoldDetailsAddressType = findViewById<TextView>(R.id.tv_sold_details_address_type)
        val tvSoldDetailsFullName = findViewById<TextView>(R.id.tv_sold_details_full_name)
        val tvSoldDetailsAddress = findViewById<TextView>(R.id.tv_sold_details_address)
        val tvSoldDetailsAdditionalNote = findViewById<TextView>(R.id.tv_sold_details_additional_note)
        val tvSoldDetailsOtherDetails = findViewById<TextView>(R.id.tv_sold_details_other_details)
        val tvSoldDetailsMobileNumber = findViewById<TextView>(R.id.tv_sold_details_mobile_number)
        val tvSoldProductSubTotal = findViewById<TextView>(R.id.tv_sold_product_sub_total)
        val tvSoldProductShippingCharge = findViewById<TextView>(R.id.tv_sold_product_shipping_charge)
        val tvSoldProductTotalAmount = findViewById<TextView>(R.id.tv_sold_product_total_amount)

        // Set the product ID
        tvSoldProductDetailsId.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object to convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance().apply {
            timeInMillis = productDetails.order_date
        }
        tvSoldProductDetailsDate.text = formatter.format(calendar.time)

        // Load product picture
        GlideLoader(this).loadProductPicture(productDetails.image, ivProductItemImage)

        // Set product details
        tvProductItemName.text = productDetails.title
        tvProductItemPrice.text = "$${productDetails.price}"
        tvSoldProductQuantity.text = productDetails.sold_quantity

        // Set address details
        with(productDetails.address) {
            tvSoldDetailsAddressType.text = type
            tvSoldDetailsFullName.text = name
            tvSoldDetailsAddress.text = "${address}, ${zipCode}"
            tvSoldDetailsAdditionalNote.text = additionalNote

            if (otherDetails.isNotEmpty()) {
                tvSoldDetailsOtherDetails.visibility = View.VISIBLE
                tvSoldDetailsOtherDetails.text = otherDetails
            } else {
                tvSoldDetailsOtherDetails.visibility = View.GONE
            }

            tvSoldDetailsMobileNumber.text = mobileNumber
        }

        // Set financial details
        tvSoldProductSubTotal.text = productDetails.sub_total_amount
        tvSoldProductShippingCharge.text = productDetails.shipping_charge
        tvSoldProductTotalAmount.text = productDetails.total_amount
    }
}