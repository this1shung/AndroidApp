package com.example.myfshop.models
import java.io.Serializable

data class Address (
    val user_id: String = "",
    val name: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val zipCode: String = "",
    val additionalNote: String = "",
    val type: String = "",
    val otherDetails: String = "",
    var id: String = "",
): Serializable
