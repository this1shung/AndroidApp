package com.example.myfshop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.TextView
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
import com.example.myfshop.utils.MSPButton

class DashboardFragment : BaseFragment() {
    private lateinit var mRootView: View
    private lateinit var productListAdapter: DashboardItemsListAdapter
    private var productList: ArrayList<Product> = ArrayList()
    private var filteredList: ArrayList<Product> = ArrayList()
    private lateinit var searchView: SearchView
    //private lateinit var searchButton: MSPButton
    private lateinit var btnAll: MSPButton
    private lateinit var btnShirt: MSPButton
    private lateinit var btnPants: MSPButton
    private var currentCategory: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize views
        searchView = mRootView.findViewById(R.id.search_view)
        //searchButton = mRootView.findViewById(R.id.btn_search)
        btnAll = mRootView.findViewById(R.id.btn_all)
        btnShirt = mRootView.findViewById(R.id.btn_shirt)
        btnPants = mRootView.findViewById(R.id.btn_pants)

        setupClickListeners()
        setupSearchView()

        return mRootView
    }

    private fun setupClickListeners() {
//        searchButton.setOnClickListener {
//            filterProducts(searchView.query.toString())
//        }

        btnAll.setOnClickListener {
            currentCategory = "all"
            filterProducts(searchView.query.toString())
        }

        btnShirt.setOnClickListener {
            currentCategory = "shirt"
            filterProducts(searchView.query.toString())
        }

        btnPants.setOnClickListener {
            currentCategory = "pant"
            filterProducts(searchView.query.toString())
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return true
            }
        })
    }

    private fun filterProducts(query: String) {
        filteredList = if (query.isEmpty() && currentCategory == "all") {
            ArrayList(productList)
        } else {
            productList.filter { product ->
                (query.isEmpty() || product.title.contains(query, ignoreCase = true)) &&
                        (currentCategory == "all" || product.category.equals(currentCategory, ignoreCase = true))
            } as ArrayList<Product>
        }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        val rvDashboardItems = mRootView.findViewById<RecyclerView>(R.id.rv_dashboard_items)
        val tvNoDashboardItemsFound = mRootView.findViewById<TextView>(R.id.tv_no_dashboard_items_found)

        if (filteredList.isNotEmpty()) {
            rvDashboardItems.visibility = View.VISIBLE
            tvNoDashboardItemsFound.visibility = View.GONE

            rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            rvDashboardItems.setHasFixedSize(true)

            productListAdapter = DashboardItemsListAdapter(requireActivity(), filteredList)
            rvDashboardItems.adapter = productListAdapter

            productListAdapter.setOnClickListener(object :
                DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    startActivity(intent)
                }
            })
        } else {
            rvDashboardItems.visibility = View.GONE
            tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        productList = dashboardItemsList
        filteredList = ArrayList(productList)
        hideProgressDialog()
        updateRecyclerView()
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
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
}