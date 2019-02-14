package com.kisabhub.weather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kisabhub.weather.R
import com.kisabhub.weather.db.WeatherData
import java.text.DateFormatSymbols

class WeatherAdapter : PagedListAdapter<WeatherData, WeatherAdapter.WeatherHolder>(WEATHER_COMPARATOR) {
    companion object {
        val WEATHER_COMPARATOR = object : DiffUtil.ItemCallback<WeatherData>() {
            override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean = oldItem == newItem

            override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean = oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_weather, parent, false)
        return WeatherHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        if (getItem(position) == null) {
            return
        }
        val (id, month, value, year, metrics, location) = getItem(position)!!
        holder.itemView.findViewById<TextView>(R.id.monthValue).text = DateFormatSymbols().months[month - 1]
        holder.itemView.findViewById<TextView>(R.id.yearValue).text = year.toString()
        holder.itemView.findViewById<TextView>(R.id.tempValue).text = value.toFloat().toString() + "\u00b0"
        val drawableId = when (metrics) {
            "Tmax" -> R.drawable.ic_max_temperature
            "Tmin" -> R.drawable.ic_min_temperature
            else -> R.drawable.ic_rainfall
        }
        holder.itemView.findViewById<ImageView>(R.id.tempIcon).setImageResource(drawableId)
    }

    inner class WeatherHolder(view: View) : RecyclerView.ViewHolder(view)
}