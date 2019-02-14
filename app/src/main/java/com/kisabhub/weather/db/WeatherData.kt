package com.kisabhub.weather.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "WeatherData")
data class WeatherData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("month")
    val month: Int = 0, // 12
    @SerializedName("value")
    val value: Double = 0.0, // 98.2
    @SerializedName("year")
    val year: Int = 0, // 2017
    @SerializedName("metrics")
    val metrics: String = "", //TMax
    @SerializedName("location")
    val location: String = "" //London
)