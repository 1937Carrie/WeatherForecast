package com.stanislavdumchykov.weatherapp.presentation.main.adapter.diffCallBack

import androidx.recyclerview.widget.DiffUtil
import com.stanislavdumchykov.weatherapp.domain.model.ShortWeatherFormat

class ItemDiffCallBack : DiffUtil.ItemCallback<ShortWeatherFormat>() {
    override fun areItemsTheSame(
        oldItem: ShortWeatherFormat, newItem: ShortWeatherFormat
    ): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(
        oldItem: ShortWeatherFormat, newItem: ShortWeatherFormat
    ): Boolean {
        return oldItem == newItem
    }

}