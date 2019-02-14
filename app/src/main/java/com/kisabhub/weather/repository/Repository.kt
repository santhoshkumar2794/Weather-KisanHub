package com.kisabhub.weather.repository

import android.util.Log
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.google.gson.Gson
import com.kisabhub.weather.db.WeatherData
import com.kisabhub.weather.db.WeatherDb
import kotlinx.coroutines.GlobalScope
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class Repository(val db: WeatherDb) {

    fun fetchTemparature(metrics: String, location: String): Listing<WeatherData> {
        val liveData = db.weather().loadWeather(metrics, location).toLiveData(
            pageSize = 10,
            boundaryCallback = WeatherBoundaryCallback(
                metrics = metrics,
                location = location,
                handleResponse = {
                    db.weather().insertWeatherData(it.subList(0,500))
                })
        )
        return Listing(
            pagedList = liveData
        )
    }
}

class WeatherBoundaryCallback(
    private val metrics: String,
    private val location: String,
    private val handleResponse: (List<WeatherData>) -> Unit
) :
    PagedList.BoundaryCallback<WeatherData>() {

    private val okHttpClient = OkHttpClient()

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Log.e("onZeroItemsLoaded","onZeroItemsLoaded    $metrics    $location")
        val url = "https://s3.eu-west-2.amazonaws.com/interview-question-data/metoffice/$metrics-$location.json"
        okHttpClient.newCall(Request.Builder().url(url).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val jsonArray = JSONArray(response.body()?.string())
                        val gson = Gson()
                        val weatherDataList = ArrayList<WeatherData>()
                        for (i in 0 until jsonArray.length()) {
                            val weatherData = gson.fromJson(jsonArray[i].toString(), WeatherData::class.java)
                            weatherDataList.add(weatherData.copy(metrics = metrics, location = location))
                        }
                        handleResponse(weatherDataList)
                    }
                }
            })
    }

    override fun onItemAtEndLoaded(itemAtEnd: WeatherData) {
        super.onItemAtEndLoaded(itemAtEnd)
    }

    override fun onItemAtFrontLoaded(itemAtFront: WeatherData) {
        super.onItemAtFrontLoaded(itemAtFront)
    }
}