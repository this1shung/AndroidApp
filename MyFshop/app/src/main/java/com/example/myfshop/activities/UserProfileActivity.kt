package com.example.myfshop.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.utils.Constants
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val iv_user_photo = findViewById<View>(R.id.iv_user_photo)
        iv_user_photo.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //Toast.makeText(this, "You already have the storage permission.", Toast.LENGTH_LONG).show()
                        Constants.showImageChooser(this)
                    } else {
                        // Requesting the permission
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            Constants.READ_MEDIA_IMAGES_PERMISSION_CODE
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_MEDIA_IMAGES_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "The storage permission is granted.", Toast.LENGTH_LONG).show()
                Constants.showImageChooser(this)
            } else {
                // Permission denied
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        val selectedImageFileUri = data.data!!
                        val ivUserPhoto = findViewById<ImageView>(R.id.iv_user_photo)
                        ivUserPhoto.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    //Xử lý kết quả của hoạt động chọn hình ảnh. Nếu thành công, lấy URI của hình ảnh đã chọn và hiển thị nó trên ImageView. Nếu có lỗi, hiển thị thông báo lỗi. Nếu người dùng hủy chọn, ghi log thông báo.

}