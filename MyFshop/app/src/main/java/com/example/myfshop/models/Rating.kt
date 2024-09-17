package com.example.myfshop.models

import java.io.Serializable

data class Rating(
    val product_id: String = "",
    val user_id: String = "",
    val rating: Float = 0f,
    var rating_id: String = ""
) : Serializable
