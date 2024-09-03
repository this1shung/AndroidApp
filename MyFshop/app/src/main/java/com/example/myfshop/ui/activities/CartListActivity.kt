package com.example.myfshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.CartItem
import com.example.myfshop.models.Product
import com.example.myfshop.ui.adapters.CartItemsListAdapter
import com.example.myfshop.utils.Constants

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()
        val btn_checkout = findViewById<TextView>(R.id.btn_checkout)
        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }
    private fun setupActionBar() {
        val toolbar_cart_list_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_cart_list_activity)

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {

                    cart.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        // Find views by their IDs.
        val rvCartItemsList = findViewById<RecyclerView>(R.id.rv_cart_items_list)
        val llCheckout = findViewById<LinearLayout>(R.id.ll_checkout)
        val tvNoCartItemFound = findViewById<TextView>(R.id.tv_no_cart_item_found)
        val tvSubTotal = findViewById<TextView>(R.id.tv_sub_total)
        val tvShippingCharge = findViewById<TextView>(R.id.tv_shipping_charge)
        val tvTotalAmount = findViewById<TextView>(R.id.tv_total_amount)

        if (mCartListItems.isNotEmpty()) {
            rvCartItemsList.visibility = View.VISIBLE
            llCheckout.visibility = View.VISIBLE
            tvNoCartItemFound.visibility = View.GONE

            rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            rvCartItemsList.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if(availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }

            tvSubTotal.text = "$$subTotal"
            tvShippingCharge.text = "$10.0"

            if (subTotal > 0) {
                llCheckout.visibility = View.VISIBLE
                val total = subTotal + 10
                tvTotalAmount.text = "$$total"
            } else {
                llCheckout.visibility = View.GONE
            }

        } else {
            rvCartItemsList.visibility = View.GONE
            llCheckout.visibility = View.GONE
            tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }


    private fun getCartItemsList() {
        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()

    }

    fun itemRemovedSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }
}