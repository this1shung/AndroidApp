package com.example.myfshop.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfshop.R
import com.example.myfshop.models.Product
import com.example.myfshop.ui.activities.EditProductActivity
import com.example.myfshop.ui.activities.ProductDetailsActivity
import com.example.myfshop.ui.fragments.ProductsFragment
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader

open class MyProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]

        if (holder is MyViewHolder) {
            // Access views using holder.itemView and set their properties
            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "$${model.price}"
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.findViewById<ImageView>(R.id.iv_item_image))

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_product).setOnClickListener {
                val intent = Intent(context, EditProductActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                fragment.startActivityForResult(intent, Constants.EDIT_PRODUCT_REQUEST_CODE)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}




//package com.example.myfshop.ui.adapters
//
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myfshop.R
//import com.example.myfshop.models.Product
//import com.example.myfshop.ui.activities.AddProductActivity
//import com.example.myfshop.ui.activities.ProductDetailsActivity
//import com.example.myfshop.ui.fragments.ProductsFragment
//import com.example.myfshop.utils.Constants
//import com.example.myfshop.utils.GlideLoader
//
//open class MyProductsListAdapter(
//    private val context: Context,
//    private var list: ArrayList<Product>,
//    private val fragment: ProductsFragment
//) : RecyclerView.Adapter<MyProductsListAdapter.MyViewHolder>() {
//
//    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val editProduct: ImageButton = view.findViewById(R.id.ib_edit_product)
//        val deleteProduct: ImageButton = view.findViewById(R.id.ib_delete_product)
//        val productName: TextView = view.findViewById(R.id.tv_item_name)
//        val productPrice: TextView = view.findViewById(R.id.tv_item_price)
//        val productImage: ImageView = view.findViewById(R.id.iv_item_image)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val view = LayoutInflater.from(context)
//            .inflate(R.layout.item_list_layout, parent, false)
//        return MyViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val product = list[position]
//
//        // Set product details to the views
//        holder.productName.text = product.title
//        holder.productPrice.text = "$${product.price}"
//        GlideLoader(context).loadProductPicture(product.image, holder.productImage)
//
//        // Handle delete button click event
//        holder.deleteProduct.setOnClickListener {
//            fragment.deleteProduct(product.product_id)
//        }
//
//        // Handle edit button click event
//        holder.editProduct.setOnClickListener {
//            val intent = Intent(context, AddProductActivity::class.java)
//            intent.putExtra("extra_product_id", product.product_id) // Pass the product ID for editing
//            fragment.startActivityForResult(intent, Constants.EDIT_PRODUCT_REQUEST_CODE)
//        }
//
//        // Handle item click event
//        holder.itemView.setOnClickListener {
//            val intent = Intent(context, ProductDetailsActivity::class.java)
//            intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
//            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
//            context.startActivity(intent)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//}
