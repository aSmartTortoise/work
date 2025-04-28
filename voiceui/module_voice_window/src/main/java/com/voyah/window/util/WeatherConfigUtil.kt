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

        when (weather.weatherDay) {
            "晴", "大部晴朗" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_sunny_day_l
                else R.drawable.icon_weather_sunny_day_s
            }

            "多云" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_cloudy_day_l
                else R.drawable.icon_weather_cloudy_day_s
            }

            "少云" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_partly_cloudy_day_l
                else R.drawable.icon_weather_partly_cloudy_day_s
            }

            "阴" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_overcast_l
                else R.drawable.icon_weather_overcast_s
            }

            "阵雨", "局部阵雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_rain_showers_day_l
                else R.drawable.icon_weather_rain_showers_day_s
            }

            "小阵雨" -> {
                weather.weatherDayIcon =
                    if (largeFlag) R.drawable.icon_weather_light_rain_showers_day_l
                    else R.drawable.icon_weather_light_rain_showers_day_s
            }

            "强阵雨" -> {
                weather.weatherDayIcon =
                    if (largeFlag) R.drawable.icon_weather_heavy_rain_showers_day_l
                    else R.drawable.icon_weather_heavy_rain_showers_day_s
            }

            "阵雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_snow_showers_day_l
                else R.drawable.icon_weather_snow_showers_day_s
            }

            "小阵雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_light_snow_showers_day_l
                else R.drawable.icon_weather_light_snow_showers_day_s
            }

            "雾" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_fog_l
                else R.drawable.icon_weather_fog_s
            }

            "冻雾" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_frost_fog_l
                else R.drawable.icon_weather_frost_fog_s
            }

            "沙尘暴" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_sandstorm_l
                else R.drawable.icon_weather_sandstorm_s
            }

            "浮尘" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_floating_dust_l
                else R.drawable.icon_weather_floating_dust_s
            }

            "尘卷风" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_dust_whirl_l
                else R.drawable.icon_weather_dust_whirl_s
            }

            "扬沙" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_blowing_sand_l
                else R.drawable.icon_weather_blowing_sand_s
            }

            "强沙尘暴" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_heavy_sandstorm_l
                else R.drawable.icon_weather_heavy_sandstorm_s
            }

            "霾" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_haze_l
                else R.drawable.icon_weather_haze_s
            }

            "雷阵雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_thoundershower_l
                else R.drawable.icon_weather_thoundershower_s
            }

            "雷电" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_thounder_light_l
                else R.drawable.icon_weather_thounder_light_s
            }

            "雷暴" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_thounderstorm_l
                else R.drawable.icon_weather_thounderstorm_s
            }

            "雷阵雨伴有冰雹" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_thoundershower_ice_storm_l
                else R.drawable.icon_weather_thoundershower_ice_storm_s
            }

            "冰雹" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_ice_storm_l
                else R.drawable.icon_weather_ice_storm_s
            }

            "冰针", "冰粒" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_ice_needle_l
                else R.drawable.icon_weather_ice_needle_s
            }

            "雨夹雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_rain_snow_l
                else R.drawable.icon_weather_rain_snow_s
            }

            "小雨", "小到中雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_light_rain_l
                else R.drawable.icon_weather_light_rain_s
            }

            "中雨", "雨", "中到大雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_rain_l
                else R.drawable.icon_weather_rain_s
            }

            "大雨", "大到暴雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_heavy_rain_l
                else R.drawable.icon_weather_heavy_rain_s
            }

            "暴雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_rainstorm_l
                else R.drawable.icon_weather_rainstorm_s
            }

            "大暴雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_heavy_rainstorm_l
                else R.drawable.icon_weather_heavy_rainstorm_s
            }

            "特大暴雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_extremly_heavy_rainstorm_l
                else R.drawable.icon_weather_extremly_heavy_rainstorm_s
            }

            "小雪", "小到中雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_light_snow_l
                else R.drawable.icon_weather_light_snow_s
            }

            "雪", "中雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_snow_l
                else R.drawable.icon_weather_snow_s
            }

            "大雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_heavy_snow_l
                else R.drawable.icon_weather_heavy_snow_s
            }

            "暴雪" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_snowstorm_l
                else R.drawable.icon_weather_snowstorm_s
            }

            "冻雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_freezing_rain_l
                else R.drawable.icon_weather_freezing_rain_s
            }

            else -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_default_l
                else R.drawable.icon_weather_default_s
            }
        }

        when (weather.weatherNight) {
            "晴", "大部晴朗" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_sunny_night_l
                else R.drawable.icon_weather_sunny_night_s
            }

            "多云" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_cloudy_night_l
                else R.drawable.icon_weather_cloudy_night_s
            }

            "少云" -> {
                weather.weatherNightIcon =
                    if (largeFlag) R.drawable.icon_weather_partly_cloudy_night_l
                    else R.drawable.icon_weather_partly_cloudy_night_s
            }

            "阴" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_overcast_l
                else R.drawable.icon_weather_overcast_s
            }

            "阵雨", "局部阵雨" -> {
                weather.weatherNightIcon =
                    if (largeFlag) R.drawable.icon_weather_rain_showers_night_l
                    else R.drawable.icon_weather_rain_showers_night_s
            }

            "小阵雨" -> {
                weather.weatherNightIcon =
                    if (largeFlag) R.drawable.icon_weather_light_rain_showers_night_l
                    else R.drawable.icon_weather_light_rain_showers_night_s
            }

            "强阵雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_heavy_rain_showers_night_l
                else R.drawable.icon_weather_heavy_rain_showers_night_s
            }

            "阵雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_snow_showers_night_l
                else R.drawable.icon_weather_snow_showers_night_s
            }

            "小阵雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_light_snow_showers_night_l
                else R.drawable.icon_weather_light_snow_showers_night_s
            }

            "雾" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_fog_l
                else R.drawable.icon_weather_fog_s
            }

            "冻雾" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_frost_fog_l
                else R.drawable.icon_weather_frost_fog_s
            }

            "沙尘暴" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_sandstorm_l
                else R.drawable.icon_weather_sandstorm_s
            }

            "浮尘" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_floating_dust_l
                else R.drawable.icon_weather_floating_dust_s
            }

            "尘卷风" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_dust_whirl_l
                else R.drawable.icon_weather_dust_whirl_s
            }

            "扬沙" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_blowing_sand_l
                else R.drawable.icon_weather_blowing_sand_s
            }

            "强沙尘暴" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_heavy_sandstorm_l
                else R.drawable.icon_weather_heavy_sandstorm_s
            }

            "霾" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_haze_l
                else R.drawable.icon_weather_haze_s
            }

            "雷阵雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_thoundershower_l
                else R.drawable.icon_weather_thoundershower_s
            }

            "雷电" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_thounder_light_l
                else R.drawable.icon_weather_thounder_light_s
            }

            "雷暴" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_thounderstorm_l
                else R.drawable.icon_weather_thounderstorm_s
            }

            "雷阵雨伴有冰雹" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_thoundershower_ice_storm_l
                else R.drawable.icon_weather_thoundershower_ice_storm_s
            }

            "冰雹" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_ice_storm_l
                else R.drawable.icon_weather_ice_storm_s
            }

            "冰针", "冰粒" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_ice_needle_l
                else R.drawable.icon_weather_ice_needle_s
            }

            "雨夹雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_rain_snow_l
                else R.drawable.icon_weather_rain_snow_s
            }

            "小雨", "小到中雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_light_rain_l
                else R.drawable.icon_weather_light_rain_s
            }

            "中雨", "雨", "中到大雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_rain_l
                else R.drawable.icon_weather_rain_s
            }

            "大雨", "大到暴雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_heavy_rain_l
                else R.drawable.icon_weather_heavy_rain_s
            }

            "暴雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_rainstorm_l
                else R.drawable.icon_weather_rainstorm_s
            }

            "大暴雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_heavy_rainstorm_l
                else R.drawable.icon_weather_heavy_rainstorm_s
            }

            "特大暴雨" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_extremly_heavy_rainstorm_l
                else R.drawable.icon_weather_extremly_heavy_rainstorm_s
            }

            "小雪", "小到中雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_light_snow_l
                else R.drawable.icon_weather_light_snow_s
            }

            "雪", "中雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_snow_l
                else R.drawable.icon_weather_snow_s
            }

            "大雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_heavy_snow_l
                else R.drawable.icon_weather_heavy_snow_s
            }

            "暴雪" -> {
                weather.weatherNightIcon = if (largeFlag) R.drawable.icon_weather_snowstorm_l
                else R.drawable.icon_weather_snowstorm_s
            }

            "冻雨" -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_freezing_rain_l
                else R.drawable.icon_weather_freezing_rain_s
            }

            else -> {
                weather.weatherDayIcon = if (largeFlag) R.drawable.icon_weather_default_l
                else R.drawable.icon_weather_default_s
            }
        }
    }
}