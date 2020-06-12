package com.saurav.foodlovers.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class RestaurantEntity (
    @PrimaryKey val restaurant_Id:Int,
//    @ColumnInfo(name="restaurant_id") val restaurantId:String,
    @ColumnInfo(name="restaurant_name") val restaurantName:String,
    @ColumnInfo(name="restaurant_rating") val restaurantRating:String,
    @ColumnInfo(name="restaurant_cost") val restaurantCost:String,
    @ColumnInfo(name="restaurant_image") val restaurantImage:String
)