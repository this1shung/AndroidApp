//package com.example.myfshop.ui.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.myfshop.R
//import com.example.myfshop.models.User
//
//class UsersListAdapter(
//    private val context: Context,
//    private val usersList: ArrayList<User>
//) : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {
//
//    private var onItemClickListener: OnItemClickListener? = null
//
//    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val ivUserImage: ImageView = view.findViewById(R.id.iv_item_image)
//        val tvUserName: TextView = view.findViewById(R.id.tv_user_name)
//        val ivEditUser: ImageView = view.findViewById(R.id.ib_edit_user)
//        val ivDeleteUser: ImageView = view.findViewById(R.id.ib_delete_user)
//
//        init {
//            ivEditUser.setOnClickListener {
//                onItemClickListener?.onEditClick(adapterPosition)
//            }
//            ivDeleteUser.setOnClickListener {
//                onItemClickListener?.onDeleteClick(adapterPosition)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val user = usersList[position]
//        Glide.with(context)
//            .load(user.image)
//            .placeholder(R.drawable.ic_user_placeholder)
//            .error(R.drawable.ic_user_placeholder)
//            .into(holder.ivUserImage)
//
//        //holder.tvUserName.text = user.name
//
//        holder.tvName.text = "${user.firstName} ${user.lastName}"
//        holder.tvEmail.text = user.email
//
//        holder.ibDelete.setOnClickListener {
//            onItemClickListener?.onDeleteClick(position)
//        }
//
//        holder.ibEdit.setOnClickListener {
//            onItemClickListener?.onEditClick(position)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return usersList.size
//    }
//
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        onItemClickListener = listener
//
//    }
//
//    interface OnItemClickListener {
//        fun onDeleteClick(position: Int)
//        fun onEditClick(position: Int)
//    }
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvName: TextView = view.findViewById(R.id.tv_user_name)
//        val tvEmail: TextView = view.findViewById(R.id.tv_user_email)
//        val ibDelete: ImageButton = view.findViewById(R.id.ib_delete_user)
//        val ibEdit: ImageButton = view.findViewById(R.id.ib_edit_user)
//    }
//}
package com.example.myfshop.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfshop.R
import com.example.myfshop.models.User

class UsersListAdapter(
    private val context: Context,
    private val usersList: ArrayList<User>
) : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUserImage: ImageView = view.findViewById(R.id.iv_item_image)
        val tvUserName: TextView = view.findViewById(R.id.tv_user_name)
        val tvEmail: TextView = view.findViewById(R.id.tv_user_email)
        val ibEditUser: ImageButton = view.findViewById(R.id.ib_edit_user)
        val ibDeleteUser: ImageButton = view.findViewById(R.id.ib_delete_user)

        init {
            ibEditUser.setOnClickListener {
                onItemClickListener?.onEditClick(adapterPosition)
            }
            ibDeleteUser.setOnClickListener {
                onItemClickListener?.onDeleteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList[position]

        // Load user image using Glide
        Glide.with(context)
            .load(user.image)
            .placeholder(R.drawable.ic_user_placeholder)
            .error(R.drawable.ic_user_placeholder)
            .into(holder.ivUserImage)

        // Set user name
        holder.tvUserName.text = "${user.firstName} ${user.lastName}"

        // Set user email
        holder.tvEmail.text = user.email
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }
}

