package com.example.myfshop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import androidx.lifecycle.ViewModelProvider
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.Order
import com.example.myfshop.ui.adapters.MyOrdersListAdapter

//import com.example.myfshop.activities.ui.notifications.NotificationsViewModel
//import com.example.myfshop.databinding.FragmentNotificationsBinding

class OrdersFragment : BaseFragment() {

    private lateinit var rvMyOrderItems: RecyclerView
    private lateinit var tvNoOrdersFound: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        // Initialize RecyclerView and TextView
        rvMyOrderItems = root.findViewById(R.id.rv_my_order_items)
        tvNoOrdersFound = root.findViewById(R.id.tv_no_orders_found)

        return root
    }

    override fun onResume() {
        super.onResume()

        getMyOrdersList()
    }

    private fun getMyOrdersList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        hideProgressDialog()

        if (ordersList.isNotEmpty()) {
            rvMyOrderItems.visibility = View.VISIBLE
            tvNoOrdersFound.visibility = View.GONE

            rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            rvMyOrderItems.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rvMyOrderItems.adapter = myOrdersAdapter
        } else {
            rvMyOrderItems.visibility = View.GONE
            tvNoOrdersFound.visibility = View.VISIBLE
        }
    }


}
