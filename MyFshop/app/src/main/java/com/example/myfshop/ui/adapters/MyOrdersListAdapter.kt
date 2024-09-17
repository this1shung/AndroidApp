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
import com.example.myfshop.models.Order
import com.example.myfshop.ui.activities.MyOrderDetailsActivity
import com.example.myfshop.utils.Constants
import com.example.myfshop.utils.GlideLoader

open class MyOrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
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

            val ivItemImage = holder.itemView.findViewById<ImageView>(R.id.iv_item_image)
            val tvItemName = holder.itemView.findViewById<TextView>(R.id.tv_item_name)
            val tvItemPrice = holder.itemView.findViewById<TextView>(R.id.tv_item_price)
            val ibDeleteProduct = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product)

            GlideLoader(context).loadProductPicture(
                model.image,
                ivItemImage
            )

            tvItemName.text = model.title
            tvItemPrice.text = "$${model.total_amount}"

            ibDeleteProduct.visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}