package com.example.myfshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.myfshop.activities.LoginActivity
import com.example.myfshop.activities.RegisterActivity
import com.example.myfshop.models.User
import com.example.myfshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name on firebase
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {


                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)

            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())


                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYFSHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }
}