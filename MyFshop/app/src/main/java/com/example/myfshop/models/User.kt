package com.example.myfshop.models
import java.io.Serializable

data class User(
    var id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0,
    val role: String = "user"
): Serializable