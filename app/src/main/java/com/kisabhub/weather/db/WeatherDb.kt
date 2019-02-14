package com.kisabhub.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherData::class], version = 1, exportSchema = false)
abstract class WeatherDb : RoomDatabase() {
    companion object {
        fun create(context: Context): WeatherDb {
            val databaseBuilder = Room.databaseBuilder(context, WeatherDb::class.java, "weather.db")
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun weather(): WeatherDao
}