package com.kisabhub.weather.db

import androidx.room.Dao
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kisabhub.weather.db.WeatherData


@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherData")
    fun loadAll(): DataSource.Factory<Int, WeatherData>

    @Query("SELECT * FROM WeatherData where metrics = :metrics and location = :location ORDER BY year DESC ")
    fun loadWeather(metrics: String, location: String): DataSource.Factory<Int, WeatherData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherData(weatherData: List<WeatherData>)
}