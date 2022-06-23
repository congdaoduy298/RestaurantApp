package com.amantech.foodrunner.model

import com.amantech.foodrunner.model.ItemOrder

data class Order(
    val restaurant_name: String? ="",
    val total_cost: Int? = 0,
    val order_placed_at: String,//try with date type also
    val food_items: List<ItemOrder>
)
