package com.kisabhub.weather.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kisabhub.weather.repository.Repository

class WeatherViewModel(private val repository: Repository) : ViewModel() {
    private val weatherState = MutableLiveData<WeatherState>()
    private val repoResult = Transformations.map(weatherState) {
        repository.fetchTemparature(it.metric, it.location)
    }

    init {
        weatherState.postValue(WeatherState())
    }

    fun locationChanged(location: String) {
        weatherState.postValue(weatherState.value?.copy(location = location))
    }

    fun metricChanged(metric: String) {
        weatherState.postValue(weatherState.value?.copy(metric = metric))
    }

    val weatherDataList = Transformations.switchMap(repoResult) { it.pagedList }
    val weatherStateData = weatherState as LiveData<WeatherState> //Making it immutable for view

    data class WeatherState(
            val metric: String = "Tmax", //Default Values
            val location: String = "UK"
    )
}