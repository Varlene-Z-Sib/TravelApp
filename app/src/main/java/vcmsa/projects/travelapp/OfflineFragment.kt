package vcmsa.projects.travelapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vcmsa.projects.travelapp.data.database.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

class OfflineFragment : Fragment() {

    private lateinit var offlineInfo: TextView
    private lateinit var weatherCardsContainer: LinearLayout
    private lateinit var clearAllBtn: MaterialButton
    private lateinit var detailContainer: LinearLayout
    private lateinit var detailTitle: TextView
    private lateinit var detailText: TextView

    private val cachedWeatherDao by lazy {
        AppDatabase.getDatabase(requireContext()).cachedWeatherDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_offline, container, false)

        offlineInfo = view.findViewById(R.id.offlineInfo)
        weatherCardsContainer = view.findViewById(R.id.weatherCardsContainer)
        clearAllBtn = view.findViewById(R.id.clearAllBtn)
        detailContainer = view.findViewById(R.id.detailContainer)
        detailTitle = view.findViewById(R.id.detailTitle)
        detailText = view.findViewById(R.id.detailText)

        observeOfflineWeather()
        setupClearAllButton()

        return view
    }

    private fun setupClearAllButton() {
        clearAllBtn.setOnClickListener {
            lifecycleScope.launch {
                cachedWeatherDao.deleteAllCachedWeather()
                Toast.makeText(requireContext(), "All offline data cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeOfflineWeather() {
        lifecycleScope.launch {
            cachedWeatherDao.getRecentCachedWeather(limit = 10).collectLatest { cachedList ->
                if (weatherCardsContainer.childCount > 1) {
                    weatherCardsContainer.removeViews(1, weatherCardsContainer.childCount - 1)
                }

                if (cachedList.isNotEmpty()) {
                    offlineInfo.text = "Offline weather data available:"
                    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

                    for (entry in cachedList) {
                        val card = MaterialCardView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { bottomMargin = 16 }
                            radius = 20f
                            cardElevation = 5f
                            setCardBackgroundColor(resources.getColor(android.R.color.white, null))

                            addView(TextView(context).apply {
                                text = buildString {
                                    append("📍 ${entry.locationName}\n")
                                    append("🌡 ${entry.temperature}°C — ${entry.weatherCondition}")
                                }
                                textSize = 16f
                                setPadding(32, 32, 32, 32)
                                setTextColor(resources.getColor(android.R.color.black, null))
                            })

                            // When tapped, show full detail below
                            setOnClickListener {
                                detailContainer.visibility = View.VISIBLE
                                detailTitle.text = "Weather Details — ${entry.locationName}"
                                detailText.text = buildString {
                                    append("Temperature: ${entry.temperature}°C\n")
                                    append("Condition: ${entry.weatherCondition}\n")
                                    append("Humidity: ${entry.humidity}%\n")
                                    append("Wind Speed: ${entry.windSpeed} m/s\n")
                                    append("Forecast Type: ${entry.forecastType}\n")
                                    append("Cached At: ${dateFormatter.format(Date(entry.cachedAt))}\n")
                                    append("Coordinates: (${entry.latitude}, ${entry.longitude})")
                                }
                            }
                        }
                        weatherCardsContainer.addView(card)
                    }
                } else {
                    offlineInfo.text = "No offline weather data available yet."
                    detailContainer.visibility = View.GONE
                }
            }
        }
    }
}
