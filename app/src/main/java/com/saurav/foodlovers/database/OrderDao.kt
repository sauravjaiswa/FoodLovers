package com.saurav.foodlovers.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)

    @Query("SELECT * FROM orders")
    fun getAllItems():MutableList<OrderEntity>

    @Query("DELETE FROM orders WHERE restaurantId=:restaurantId")
    fun deleteAllOrders(restaurantId: String)
}