package com.example.myfshop.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfshop.R
import com.example.myfshop.models.Order
import com.example.myfshop.ui.adapters.CartItemsListAdapter
import com.example.myfshop.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_order_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()

        lateinit var myOrderDetails: Order

        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails = intent.getSerializableExtra(Constants.EXTRA_MY_ORDER_DETAILS) as Order
        }

        setupUI(myOrderDetails)


    }
    private fun setupActionBar() {

        val toolbar_my_order_details_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_order_details_activity)
        setSupportActionBar(toolbar_my_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(orderDetails: Order) {
        // Find the TextViews by their IDs
        val tvOrderDetailsId = findViewById<TextView>(R.id.tv_order_details_id)
        val tvOrderDetailsDate = findViewById<TextView>(R.id.tv_order_details_date)
        val tvOrderStatus = findViewById<TextView>(R.id.tv_order_status)

        // Set the order ID or title
        tvOrderDetailsId.text = orderDetails.title

        // Step 6: Set the Date in the UI
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        tvOrderDetailsDate.text = orderDateTime

        // Step 7: Set the order status based on the time
        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")

        when {
            diffInHours < 1 -> {
                tvOrderStatus.text = resources.getString(R.string.order_status_pending)
                tvOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrderDetailsActivity, R.color.colorAccent))
            }

            diffInHours < 2 -> {
                tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
                tvOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrderDetailsActivity, R.color.colorOrderStatusInProcess))
            }

            else -> {
                tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
                tvOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrderDetailsActivity, R.color.colorOrderStatusDelivered))
            }
        }
        val rv_my_order_items_list = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_my_order_items_list)
        val tv_my_order_details_address_type = findViewById<TextView>(R.id.tv_my_order_details_address_type)
        val tv_my_order_details_full_name = findViewById<TextView>(R.id.tv_my_order_details_full_name)
        val tv_my_order_details_address = findViewById<TextView>(R.id.tv_my_order_details_address)
        val tv_my_order_details_additional_note = findViewById<TextView>(R.id.tv_my_order_details_additional_note)
        val tv_my_order_details_other_details = findViewById<TextView>(R.id.tv_my_order_details_other_details)
        val tv_my_order_details_mobile_number = findViewById<TextView>(R.id.tv_my_order_details_mobile_number)
        val tv_order_details_sub_total = findViewById<TextView>(R.id.tv_order_details_sub_total)
        val tv_order_details_shipping_charge = findViewById<TextView>(R.id.tv_order_details_shipping_charge)
        val tv_order_details_total_amount = findViewById<TextView>(R.id.tv_order_details_total_amount)

        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        } else {
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = orderDetails.shipping_charge
        tv_order_details_total_amount.text = orderDetails.total_amount
    }

}