package com.voyah.window.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.voyah.cockpit.window.model.Weather
import com.voyah.window.R
import com.voyah.window.model.WeatherConfig
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 *  author : jie wang
 *  date : 2024/6/7 17:53
 *  description :
 */
object WeatherConfigUtil {

    fun readSamples(context: Context): List<WeatherConfig> {
        try {
            context.assets.open("weather_id_icon.json").use { inputStream ->
                return readSamples(
                    inputStream
                )
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun readSamples(inputStream: InputStream): List<WeatherConfig> {
        val gson = Gson()
        return gson.fromJson(
            InputStreamReader(inputStream),
            object : TypeToken<List<WeatherConfig>>() {}.type
        )
    }

    fun setWeatherIcon(weather: Weather, largeFlag: Boolean = true) {
        val weatherDayIcon = when (weather.weatherDay) {
            "晴", "大部晴朗" -> {
                R.drawable.icon_weather_sunny_day
            }

            "多云" -> {
                R.drawable.icon_weather_cloudy_day
            }

            "少云" -> {
                R.drawable.icon_weather_partly_cloudy_day
            }

            "阴" -> {
                R.drawable.icon_weather_overcast
            }

            "阵雨", "局部阵雨" -> {
                R.drawable.icon_weather_rain_showers_day
            }

            "小阵雨" -> {
                R.drawable.icon_weather_light_rain_showers_day
            }

            "强阵雨" -> {
                R.drawable.icon_weather_heavy_rain_showers_day
            }

            "阵雪" -> {
                R.drawable.icon_weather_snow_showers_day
            }

            "小阵雪" -> {
                R.drawable.icon_weather_light_snow_showers_day
            }

            "雾" -> {
                R.drawable.icon_weather_fog
            }

            "冻雾" -> {
                R.drawable.icon_weather_frost_fog
            }

            "沙尘暴" -> {
                R.drawable.icon_weather_sandstorm
            }

            "浮尘" -> {
                R.drawable.icon_weather_floating_dust
            }

            "尘卷风" -> {
                R.drawable.icon_weather_dust_whirl
            }

            "扬沙" -> {
                R.drawable.icon_weather_blowing_sand
            }

            "强沙尘暴" -> {
                R.drawable.icon_weather_heavy_sandstorm
            }

            "霾" -> {
                R.drawable.icon_weather_haze
            }

            "雷阵雨" -> {
                R.drawable.icon_weather_thoundershower
            }

            "雷电" -> {
                R.drawable.icon_weather_thounder_light
            }

            "雷暴" -> {
                R.drawable.icon_weather_thounderstorm
            }

            "雷阵雨伴有冰雹" -> {
                R.drawable.icon_weather_thoundershower_ice_storm
            }

            "冰雹" -> {
                R.drawable.icon_weather_ice_storm
            }

            "冰针", "冰粒" -> {
                R.drawable.icon_weather_ice_needle
            }

            "雨夹雪" -> {
                R.drawable.icon_weather_rain_snow
            }

            "小雨", "小到中雨" -> {
                R.drawable.icon_weather_light_rain
            }

            "中雨", "雨", "中到大雨" -> {
                R.drawable.icon_weather_rain
            }

            "大雨", "大到暴雨" -> {
                R.drawable.icon_weather_heavy_rain
            }

            "暴雨" -> {
                R.drawable.icon_weather_rainstorm
            }

            "大暴雨" -> {
                R.drawable.icon_weather_heavy_rainstorm
            }

            "特大暴雨" -> {
                R.drawable.icon_weather_extremly_heavy_rainstorm
            }

            "小雪", "小到中雪" -> {
                R.drawable.icon_weather_light_snow
            }

            "雪", "中雪" -> {
                R.drawable.icon_weather_snow
            }

            "大雪" -> {
                R.drawable.icon_weather_heavy_snow
            }

            "暴雪" -> {
                R.drawable.icon_weather_snowstorm
            }

            "冻雨" -> {
                R.drawable.icon_weather_freezing_rain
            }

            else -> {
                R.drawable.icon_weather_default
            }
        }

        weather.weatherDayIcon = weatherDayIcon

        val weatherNightIcon = when (weather.weatherNight) {
            "晴", "大部晴朗" -> {
                R.drawable.icon_weather_sunny_night
            }

            "多云" -> {
                R.drawable.icon_weather_cloudy_night
            }

            "少云" -> {
                R.drawable.icon_weather_partly_cloudy_night
            }

            "阴" -> {
                R.drawable.icon_weather_overcast
            }

            "阵雨", "局部阵雨" -> {
                R.drawable.icon_weather_rain_showers_night
            }

            "小阵雨" -> {
                R.drawable.icon_weather_light_rain_showers_night
            }

            "强阵雨" -> {
                R.drawable.icon_weather_heavy_rain_showers_night
            }

            "阵雪" -> {
                R.drawable.icon_weather_snow_showers_night
            }

            "小阵雪" -> {
                R.drawable.icon_weather_light_snow_showers_night
            }

            "雾" -> {
                R.drawable.icon_weather_fog
            }

            "冻雾" -> {
                R.drawable.icon_weather_frost_fog
            }

            "沙尘暴" -> {
                R.drawable.icon_weather_sandstorm
            }

            "浮尘" -> {
                R.drawable.icon_weather_floating_dust
            }

            "尘卷风" -> {
                R.drawable.icon_weather_dust_whirl
            }

            "扬沙" -> {
                R.drawable.icon_weather_blowing_sand
            }

            "强沙尘暴" -> {
                R.drawable.icon_weather_heavy_sandstorm
            }

            "霾" -> {
                R.drawable.icon_weather_haze
            }

            "雷阵雨" -> {
                R.drawable.icon_weather_thoundershower
            }

            "雷电" -> {
                R.drawable.icon_weather_thounder_light
            }

            "雷暴" -> {
                R.drawable.icon_weather_thounderstorm
            }

            "雷阵雨伴有冰雹" -> {
                R.drawable.icon_weather_thoundershower_ice_storm
            }

            "冰雹" -> {
                R.drawable.icon_weather_ice_storm
            }

            "冰针", "冰粒" -> {
                R.drawable.icon_weather_ice_needle
            }

            "雨夹雪" -> {
                R.drawable.icon_weather_rain_snow
            }

            "小雨", "小到中雨" -> {
                R.drawable.icon_weather_light_rain
            }

            "中雨", "雨", "中到大雨" -> {
                R.drawable.icon_weather_rain
            }

            "大雨", "大到暴雨" -> {
                R.drawable.icon_weather_heavy_rain
            }

            "暴雨" -> {
                R.drawable.icon_weather_rainstorm
            }

            "大暴雨" -> {
                R.drawable.icon_weather_heavy_rainstorm
            }

            "特大暴雨" -> {
                R.drawable.icon_weather_extremly_heavy_rainstorm
            }

            "小雪", "小到中雪" -> {
                R.drawable.icon_weather_light_snow
            }

            "雪", "中雪" -> {
                R.drawable.icon_weather_snow
            }

            "大雪" -> {
                R.drawable.icon_weather_heavy_snow
            }

            "暴雪" -> {
                R.drawable.icon_weather_snowstorm
            }

            "冻雨" -> {
                R.drawable.icon_weather_freezing_rain
            }

            else -> {
                R.drawable.icon_weather_default
            }
        }
        weather.weatherNightIcon = weatherNightIcon
    }
}