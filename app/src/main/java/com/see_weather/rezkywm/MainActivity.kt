package com.see_weather.rezkywm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val name: String
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("q") location: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = weatherService.getWeatherData("Manado", "IS_HIDDEN")

                runOnUiThread {
                    val currentDate = Date()
                    val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(currentDate)
                    val dateTextView = findViewById<TextView>(R.id.dateTextView)
                    val temperatureTextView = findViewById<TextView>(R.id.temperatureTextView)
                    val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
                    val humidityTextView = findViewById<TextView>(R.id.humidityTextView)
                    val pressureTextView = findViewById<TextView>(R.id.pressureTextView)
                    val windSpeedTextView = findViewById<TextView>(R.id.windSpeedTextView)
                    val temperatureKelvin = response.main.temp
                    val temperatureCelcius = temperatureKelvin - 273.15
                    val weatherIcon = response.weather[0].icon
                    val imageView = findViewById<ImageView>(R.id.weatherImageView)
                    val descriptionText = response.weather[0].description
                    val titleCaseDescription = descriptionText.split(" ")
                        .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
                    temperatureTextView.text = getString(R.string.temperature, String.format("%.0f", temperatureCelcius))
                    descriptionTextView.text = getString(R.string.description, titleCaseDescription)
                    val windSpeed = response.wind.speed.toString()
                    val humidityPercentage = response.main.humidity.toString()
                    val pressureValue = response.main.pressure.toString()
                    val windSpeedText = "<b>$windSpeed</b> km/h"
                    val humidityText = "<b>$humidityPercentage</b>%"
                    val pressureText = "<b>$pressureValue</b> hPa"

                    when (weatherIcon) {
                        "01d" -> imageView.setImageResource(R.drawable.onedn)
                        "02d" -> imageView.setImageResource(R.drawable.twodn)
                        "03d" -> imageView.setImageResource(R.drawable.threedn)
                        "04d" -> imageView.setImageResource(R.drawable.fourdn)
                        "09d" -> imageView.setImageResource(R.drawable.fivedn)
                        "10d" -> imageView.setImageResource(R.drawable.sixdn)
                        "11d" -> imageView.setImageResource(R.drawable.sevendn)
                        "13d" -> imageView.setImageResource(R.drawable.eightdn)
                        "50d" -> imageView.setImageResource(R.drawable.ninedn)

                        "01n" -> imageView.setImageResource(R.drawable.onedn)
                        "02n" -> imageView.setImageResource(R.drawable.twodn)
                        "03n" -> imageView.setImageResource(R.drawable.threedn)
                        "04n" -> imageView.setImageResource(R.drawable.fourdn)
                        "09n" -> imageView.setImageResource(R.drawable.fivedn)
                        "10n" -> imageView.setImageResource(R.drawable.sixdn)
                        "11n" -> imageView.setImageResource(R.drawable.sevendn)
                        "13n" -> imageView.setImageResource(R.drawable.eightdn)
                        "50n" -> imageView.setImageResource(R.drawable.ninedn)
                    }

                    dateTextView.text = formattedDate
                    windSpeedTextView.text = Html.fromHtml(windSpeedText, Html.FROM_HTML_MODE_COMPACT)
                    humidityTextView.text = Html.fromHtml(humidityText, Html.FROM_HTML_MODE_COMPACT)
                    pressureTextView.text = Html.fromHtml(pressureText, Html.FROM_HTML_MODE_COMPACT)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}