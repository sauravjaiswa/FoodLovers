package com.saurav.foodlovers.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM favorites")
    fun getAllRestaurants():MutableList<RestaurantEntity>

    @Query("SELECT * FROM favorites WHERE restaurant_Id=:restaurantId")
    fun getRestaurantById(restaurantId:Int):RestaurantEntity
}