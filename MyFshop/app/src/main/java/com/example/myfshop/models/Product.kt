package com.example.myfshop.models

import java.io.Serializable

data class Product(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val stock_quantity: String = "",
    val image: String = "",
    var product_id: String = "",
    val category: String = "",
    var average_rating: Float = 0.0f,
    var total_ratings: Int = 0,
) : Serializable



