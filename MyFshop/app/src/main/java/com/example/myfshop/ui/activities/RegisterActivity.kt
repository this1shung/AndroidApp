package com.example.myfshop.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfshop.R
import com.example.myfshop.firestore.FirestoreClass
import com.example.myfshop.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        val btnRegister: Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            registerUser()
        }

        val tvlogin: TextView = findViewById(R.id.tv_login)
        tvlogin.setOnClickListener {
            //startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            onBackPressed()
        }
    }

    private fun setupActionBar() {

        val toolbar: Toolbar = findViewById(R.id.toolbar_register_activity)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        // Tìm các view bằng ID và gán vào các biến
        val firstNameEditText = findViewById<EditText>(R.id.et_first_name)
        val lastNameEditText = findViewById<EditText>(R.id.et_last_name)
        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.et_confirm_password)
        val termsCheckBox = findViewById<CheckBox>(R.id.cb_terms_and_condition)

        return when {
            TextUtils.isEmpty(firstNameEditText.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(lastNameEditText.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(emailEditText.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(passwordEditText.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(confirmPasswordEditText.text.toString().trim()) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            passwordEditText.text.toString().trim() != confirmPasswordEditText.text.toString()
                .trim() -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }

            !termsCheckBox.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }

            else -> {
                //showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    private fun registerUser() {
        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val firstNameEditText = findViewById<EditText>(R.id.et_first_name)
        val lastNameEditText = findViewById<EditText>(R.id.et_last_name)

        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = emailEditText.text.toString().trim()
            val password: String = passwordEditText.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val firebaseUser: FirebaseUser = task.result?.user!!

                        val user = User(
                            firebaseUser.uid,
                            firstNameEditText.text.toString().trim(),
                            lastNameEditText.text.toString().trim(),
                            emailEditText.text.toString().trim()
                        )

                        FirestoreClass().registerUser(this@RegisterActivity, user)

                        showErrorSnackBar(
                            "You are registered successfully. Your user id is ${firebaseUser.uid}",
                            false
                        )


                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
        }
    }
    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()

        FirebaseAuth.getInstance().signOut()
        finish()
    }
}