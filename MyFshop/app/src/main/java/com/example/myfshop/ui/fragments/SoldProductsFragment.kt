package com.example.myfshop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.SoldProduct
import com.example.myfshop.ui.adapters.SoldProductsListAdapter

class SoldProductsFragment : BaseFragment() {

    private var _rootView: View? = null
    private val rootView get() = _rootView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _rootView = inflater.inflate(R.layout.fragment_sold_products, container, false)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()

        if (_rootView == null) {
            return
        }

        val rvSoldProductItems = rootView.findViewById<RecyclerView>(R.id.rv_sold_product_items)
        val tvNoSoldProductsFound = rootView.findViewById<TextView>(R.id.tv_no_sold_products_found)

        if (soldProductsList.size > 0) {
            rvSoldProductItems.visibility = View.VISIBLE
            tvNoSoldProductsFound.visibility = View.GONE

            rvSoldProductItems.layoutManager = LinearLayoutManager(activity)
            rvSoldProductItems.setHasFixedSize(true)

            val soldProductsListAdapter = SoldProductsListAdapter(requireActivity(), soldProductsList)
            rvSoldProductItems.adapter = soldProductsListAdapter
        } else {
            rvSoldProductItems.visibility = View.GONE
            tvNoSoldProductsFound.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _rootView = null
    }
}