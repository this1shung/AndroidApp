package com.example.myfshop.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap


object Constants {
    const val  USERS: String = "users"
    const val  MYFSHOP_PREFERENCES: String = "MyFshopPrefs"
    const val  LOGGED_IN_USERNAME: String = "logged_in_username"
    const val  READ_MEDIA_IMAGES_PERMISSION_CODE = 2
    const val  PICK_IMAGE_REQUEST_CODE = 1

    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}