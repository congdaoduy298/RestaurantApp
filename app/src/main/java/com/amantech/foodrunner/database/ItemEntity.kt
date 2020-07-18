package com.amantech.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Cart")
data class ItemEntity(
    @PrimaryKey val item_id: Int,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "item_price") val itemPrice: String
)