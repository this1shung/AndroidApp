package com.example.myfshop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.User
import com.example.myfshop.ui.activities.EditUserProfileActivity
import com.example.myfshop.ui.adapters.UsersListAdapter

class UserFragment : Fragment() {

    private lateinit var mUsersList: ArrayList<User>
    private lateinit var mAdapter: UsersListAdapter
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_users, container, false)

        mRecyclerView = root.findViewById(R.id.rv_users_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.setHasFixedSize(true)

        return root
    }

    override fun onResume() {
        super.onResume()
        getUsersList()
    }

    fun onEditClick(position: Int) {
        val intent = Intent(activity, EditUserProfileActivity::class.java)
        intent.putExtra("user_id", mUsersList[position].id)
        startActivity(intent)
    }


    private fun getUsersList() {
        FirestoreClass().getUsersList(this@UserFragment)
    }

    fun successUsersListFromFireStore(usersList: ArrayList<User>) {
        mUsersList = usersList

        if (mUsersList.size > 0) {
            mRecyclerView.visibility = View.VISIBLE

            mAdapter = UsersListAdapter(requireContext(), mUsersList)
            mRecyclerView.adapter = mAdapter

            mAdapter.setOnItemClickListener(object : UsersListAdapter.OnItemClickListener {
                override fun onDeleteClick(position: Int) {
                    deleteUser(mUsersList[position].id)
                }

                override fun onEditClick(position: Int) {
                    val intent = Intent(activity, EditUserProfileActivity::class.java)
                    intent.putExtra("user_id", mUsersList[position].id)
                    startActivity(intent)
                }
            })

        } else {
            mRecyclerView.visibility = View.GONE
        }
    }

    private fun deleteUser(userId: String) {
        FirestoreClass().deleteUser(this@UserFragment, userId)
    }

    fun userDeleteSuccess() {
        Toast.makeText(activity, "User deleted successfully", Toast.LENGTH_SHORT).show()
        getUsersList()
    }

    fun userDeleteFailure(e: Exception) {
        Log.e(javaClass.simpleName, "Error while deleting the user.", e)
        Toast.makeText(activity, "Error while deleting the user", Toast.LENGTH_SHORT).show()
    }
}

