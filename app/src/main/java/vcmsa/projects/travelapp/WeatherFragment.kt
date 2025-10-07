package vcmsa.projects.travelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import vcmsa.projects.travelapp.data.database.AppDatabase
import vcmsa.projects.travelapp.data.model.RecentSearch
import vcmsa.projects.travelapp.data.repository.WeatherRepository
import vcmsa.projects.travelapp.network.RetrofitClient
import vcmsa.projects.travelapp.RecentSearchAdapter

class WeatherFragment : Fragment() {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var cityInput: TextInputEditText
    private lateinit var checkWeatherButton: MaterialButton
    private lateinit var weatherInfo: TextView
    private lateinit var recentRecycler: RecyclerView
    private lateinit var recentSearchAdapter: RecentSearchAdapter

    private val WEATHER_API_KEY = "12304347a23bca4027937483ec2f7321"

    private val recentSearchDao by lazy {
        AppDatabase.getDatabase(requireContext()).recentSearchDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        initializeRepository()
        initializeViews(view)
        setupListeners()
        updateRecentSearches()

        return view
    }

    private fun initializeRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        weatherRepository = WeatherRepository(
            RetrofitClient.weatherApi,
            database.cachedWeatherDao(),
            WEATHER_API_KEY
        )
    }

    private fun initializeViews(view: View) {
        cityInput = view.findViewById(R.id.weatherLocation)
        checkWeatherButton = view.findViewById(R.id.checkWeatherBtn)
        weatherInfo = view.findViewById(R.id.weatherInfo)
        recentRecycler = view.findViewById(R.id.recentSearchesList)

        recentRecycler.layoutManager = LinearLayoutManager(requireContext())
        recentSearchAdapter = RecentSearchAdapter(emptyList()) { search ->
            fetchWeather(search.city) // Clicking search refetches weather
        }
        recentRecycler.adapter = recentSearchAdapter
    }

    private fun setupListeners() {
        checkWeatherButton.setOnClickListener {
            val city = cityInput.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(cityName: String) {
        lifecycleScope.launch {
            try {
                checkWeatherButton.isEnabled = false
                checkWeatherButton.text = "Loading..."
                weatherInfo.text = "Fetching weather data..."

                val weather = weatherRepository.getWeatherByCity(cityName)
                if (weather != null) {
                    displayWeather(weather)

                    // Save city to recent searches
                    recentSearchDao.insertSearch(RecentSearch(city = cityName))
                    updateRecentSearches()
                } else {
                    weatherInfo.text = "Weather data not available"
                    Toast.makeText(requireContext(), "Weather data not available", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                weatherInfo.text = "Error loading weather data"
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                checkWeatherButton.isEnabled = true
                checkWeatherButton.text = "Check Weather"
            }
        }
    }

    private fun displayWeather(weather: vcmsa.projects.travelapp.data.model.WeatherResponse) {
        val weatherText = buildString {
            append("Location: ${weather.name}, ${weather.sys.country}\n\n")
            append("Temperature: ${weather.main.temp.toInt()}°C\n")
            append("Feels Like: ${weather.main.feelsLike.toInt()}°C\n")
            append("Condition: ${weather.weather.firstOrNull()?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            } ?: "Unknown"}\n\n")
            append("Humidity: ${weather.main.humidity}%\n")
            append("Wind Speed: ${weather.wind.speed} m/s\n")
            append("Pressure: ${weather.main.pressure} hPa")
        }
        weatherInfo.text = weatherText
    }

    private fun updateRecentSearches() {
        lifecycleScope.launch {
            val searches = recentSearchDao.getRecentSearches()
            recentSearchAdapter.updateData(searches)
        }
    }
}
