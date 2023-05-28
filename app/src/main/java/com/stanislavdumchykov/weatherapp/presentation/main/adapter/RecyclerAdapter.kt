package com.stanislavdumchykov.weatherapp.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.databinding.RecyclerViewItemBinding
import com.stanislavdumchykov.weatherapp.domain.model.ShortWeatherFormat
import com.stanislavdumchykov.weatherapp.presentation.main.adapter.diffCallBack.ItemDiffCallBack

class RecyclerAdapter(
    private val weatherInterpretation: (Int) -> Int
) : ListAdapter<ShortWeatherFormat, RecyclerAdapter.WeatherViewHolder>(
    ItemDiffCallBack()
) {

    inner class WeatherViewHolder(
        private val binding: RecyclerViewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(item: ShortWeatherFormat) {
            with(binding) {
                textViewWeatherHourlyTemperature.text = binding.root.context.getString(
                    R.string.temperature_value_short,
                    item.temperature
                )
                textViewWeatherHourlyDescription.text = binding.root.context.getString(
                    weatherInterpretation(item.weatherCode)
                )
                textViewWeatherHourlyTime.text =
                    item.time.substring(item.time.length - currentList.size, item.time.length)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {

        val weatherViewHolder = WeatherViewHolder(
            RecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            itemView.layoutParams.width = parent.width / currentList.size
        }

        return weatherViewHolder
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}
