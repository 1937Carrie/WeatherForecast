package com.stanislavdumchykov.weatherapp.presentation.main.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stanislavdumchykov.weatherapp.data.repository.WeatherInterpretationCodeImpl
import com.stanislavdumchykov.weatherapp.databinding.RecyclerViewItemBinding
import com.stanislavdumchykov.weatherapp.domain.model.ShortWeatherFormat
import com.stanislavdumchykov.weatherapp.presentation.main.adapter.diffCallBack.ItemDiffCallBack
import kotlin.math.roundToInt

class RecyclerAdapter(private val recyclerWidth: Int) :
    ListAdapter<ShortWeatherFormat, RecyclerAdapter.WeatherViewHolder>(
        ItemDiffCallBack()
    ) {

    inner class WeatherViewHolder(
        private val binding: RecyclerViewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(item: ShortWeatherFormat) {
            with(binding) {
                textViewWeatherHourlyTemperature.text = item.temperature.toString()
                textViewWeatherHourlyDescription.text = binding.root.context.getString(
                    WeatherInterpretationCodeImpl.getDescriptionByCode(item.weatherCode)
                )
                textViewWeatherHourlyTime.text =
                    item.time.substring(item.time.length - 5, item.time.length)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            RecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = (recyclerWidth.dp - 32.dp) / 5
        holder.itemView.layoutParams = layoutParams

        holder.bindTo(getItem(position))
    }

}

private val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()