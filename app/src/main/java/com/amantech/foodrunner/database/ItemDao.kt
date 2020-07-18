package com.amantech.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {

    @Insert
    fun insertItem(itemEntity: ItemEntity)

    @Delete
    fun deleteItem(itemEntity: ItemEntity)

    @Query(value="DELETE FROM Cart")
    fun clearCart()

    @Query(value = "SELECT * FROM Cart")
    fun getAllItems(): List<ItemEntity>

    @Query(value = "SELECT * FROM Cart WHERE item_id = :itemId")
    fun getItemById(itemId: String): ItemEntity
}