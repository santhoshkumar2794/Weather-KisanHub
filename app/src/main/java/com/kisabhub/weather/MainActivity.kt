package com.kisabhub.weather

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kisabhub.weather.db.WeatherData
import com.kisabhub.weather.db.WeatherDb
import com.kisabhub.weather.model.WeatherViewModel
import com.kisabhub.weather.repository.Repository
import com.kisabhub.weather.ui.WeatherAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val metricItems = arrayOf("Max Temp", "Min Temp", "Rainfall")
    private val locationItems = arrayOf("UK", "England", "Scotland", "Wales")
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        location.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("Select a location")
                    .setSingleChoiceItems(locationItems, getCurrentLocationIndex()) { dialog, which ->
                        viewModel.locationChanged(locationItems[which])
                        dialog.dismiss()
                    }
                    .show()
        }

        weatherRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = WeatherAdapter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weather_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.filter) {
            AlertDialog.Builder(this)
                    .setTitle("Select a category")
                    .setSingleChoiceItems(metricItems, getCurrentMetricIndex()) { dialog, which ->
                        val metric = when (metricItems[which]) {
                            "Max Temp" -> "Tmax"
                            "Min Temp" -> "Tmin"
                            else -> "Rainfall"
                        }
                        viewModel.metricChanged(metric)
                        dialog.dismiss()
                    }
                    .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        viewModel = getViewModel()
        viewModel.weatherDataList.observe(
                this,
                Observer<PagedList<WeatherData>> {
                    (weatherRecyclerView.adapter as WeatherAdapter).submitList(it)
                }
        )

        viewModel.weatherStateData.observe(
                this,
                Observer<WeatherViewModel.WeatherState> {
                    location.text = it.location
                }
        )
    }

    private fun getViewModel(): WeatherViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val db by lazy {
                    WeatherDb.create(this@MainActivity)
                }
                val repo = Repository(db)
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(repo) as T
            }
        })[WeatherViewModel::class.java]
    }

    private fun getCurrentMetricIndex(): Int {
        return when (viewModel.weatherStateData.value!!.metric) {
            "Tmax" -> 0
            "Tmin" -> 1
            else -> 2
        }
    }

    private fun getCurrentLocationIndex(): Int = locationItems.indexOf(viewModel.weatherStateData.value!!.location)

}
