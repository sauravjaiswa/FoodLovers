package com.saurav.foodlovers.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity (
//    @PrimaryKey val orderNumber:Int,
    @PrimaryKey val restaurantId:String,
    @ColumnInfo(name="food_items") val foodItems:String
)