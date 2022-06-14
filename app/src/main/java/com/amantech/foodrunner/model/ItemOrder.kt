package com.amantech.foodrunner.model

data class ItemOrder(
    var price: Int?=0,
    var count: Int?=0,
    var name: String?="",
    var url : String?=""
)