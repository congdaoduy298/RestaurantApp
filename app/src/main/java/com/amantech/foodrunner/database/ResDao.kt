package com.amantech.foodrunner.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {
    @Insert
    fun insertRestaurant(bookEntity: ResEntity)

    @Delete
    fun deleteRestaurant(bookEntity: ResEntity)

    @Query(value="SELECT * FROM Restaurants")
    fun getAllRestaurants(): List<ResEntity>

    @Query(value="SELECT * FROM Restaurants WHERE res_id = :resId")
    fun getRestaurantById(resId:String):ResEntity
}