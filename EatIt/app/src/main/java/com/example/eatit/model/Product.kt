package com.example.eatit.model

data class Product(
    var name: String = "",
    var description: String = "",
    var price: Float = 0.0f,
    var id: String? = "",
    var section: String = ""
)