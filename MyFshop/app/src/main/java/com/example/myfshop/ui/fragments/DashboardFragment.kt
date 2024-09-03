package com.example.myfshop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.Product
import com.example.myfshop.ui.activities.CartListActivity
import com.example.myfshop.ui.activities.ProductDetailsActivity
import com.example.myfshop.ui.activities.SettingsActivity
import com.example.myfshop.ui.adapters.DashboardItemsListAdapter
import com.example.myfshop.utils.Constants

class DashboardFragment : BaseFragment() {

    private lateinit var mRootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize mRootView here
        mRootView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return mRootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {

        val rvdashboarditems = mRootView.findViewById<RecyclerView>(R.id.rv_dashboard_items)
        val tvnodashboarditemsfound = mRootView.findViewById<TextView>(R.id.tv_no_dashboard_items_found)
        // Hide the progress dialog.
        hideProgressDialog()

        if (dashboardItemsList.size > 0) {

            rvdashboarditems.visibility = View.VISIBLE
            tvnodashboarditemsfound.visibility = View.GONE

            rvdashboarditems.layoutManager = GridLayoutManager(activity, 2)
            rvdashboarditems.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rvdashboarditems.adapter = adapter

            adapter.setOnClickListener(object :
                DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    startActivity(intent)
                }
            })
        } else {
            rvdashboarditems.visibility = View.GONE
            tvnodashboarditemsfound.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    override fun onResume() {
        super.onResume()

        getDashboardItemsList()
    }
}
