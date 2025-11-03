package vcmsa.projects.travelapp.data.repository

import vcmsa.projects.travelapp.api.WeatherApiService
import vcmsa.projects.travelapp.data.dao.CachedWeatherDao
import vcmsa.projects.travelapp.data.entity.CachedWeather
import vcmsa.projects.travelapp.data.model.WeatherResponse
import java.util.concurrent.TimeUnit

class WeatherRepository(
    private val weatherApi: WeatherApiService,
    private val cachedWeatherDao: CachedWeatherDao,
    private val apiKey: String
) {
    
    companion object {
        private val CACHE_EXPIRY_MILLIS = TimeUnit.HOURS.toMillis(1) // 1 hour cache
    }
    
    suspend fun getCurrentWeather(lat: Double, lng: Double, forceRefresh: Boolean = false): WeatherResponse? {
        return try {
            // Check cache first if not forcing refresh
            if (!forceRefresh) {
                val cached = cachedWeatherDao.getCachedWeather(lat, lng, "current")
                if (cached != null && isCacheValid(cached.cachedAt)) {
                    return convertToWeatherResponse(cached)
                }
            }
            
            // Fetch from API
            val response = weatherApi.getCurrentWeather(lat, lng, apiKey)
            
            // Cache the result
            cacheWeather(response, lat, lng, "current")
            
            response
        } catch (e: Exception) {
            // If API fails, return cached data if available
            val cached = cachedWeatherDao.getCachedWeather(lat, lng, "current")
            cached?.let { convertToWeatherResponse(it) }
        }
    }

    suspend fun getWeatherByCity(cityName: String, forceRefresh: Boolean = false): WeatherResponse? {
        return try {
            // If not forcing refresh, try to load from cache first
            if (!forceRefresh) {
                val cached = cachedWeatherDao.getCachedWeatherByName(cityName)
                if (cached != null && isCacheValid(cached.cachedAt)) {
                    return convertToWeatherResponse(cached)
                }
            }

            // Fetch from API if online
            val response = weatherApi.getWeatherByCity(cityName, apiKey)

            // Cache the API result
            val coord = response.coord
            cacheWeather(response, coord.lat, coord.lon, "current")

            response
        } catch (e: Exception) {
            // If offline or API fails — return cached data if available
            val cached = cachedWeatherDao.getCachedWeatherByName(cityName)
            cached?.let { convertToWeatherResponse(it) }
        }
    }


    private suspend fun cacheWeather(response: WeatherResponse, lat: Double, lng: Double, type: String) {
        val cached = CachedWeather(
            locationName = response.name,
            latitude = lat,
            longitude = lng,
            temperature = response.main.temp,
            weatherCondition = response.weather.firstOrNull()?.main ?: "Unknown",
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            forecastType = type
        )
        cachedWeatherDao.insertCachedWeather(cached)
    }
    
    private fun isCacheValid(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt < CACHE_EXPIRY_MILLIS
    }
    
    private fun convertToWeatherResponse(cached: CachedWeather): WeatherResponse {
        // Convert cached data back to WeatherResponse format
        // This is a simplified version - you may need to enhance this
        return WeatherResponse(
            coord = vcmsa.projects.travelapp.data.model.Coord(cached.longitude, cached.latitude),
            weather = listOf(vcmsa.projects.travelapp.data.model.Weather(0, cached.weatherCondition, cached.weatherCondition, "")),
            base = "",
            main = vcmsa.projects.travelapp.data.model.Main(
                temp = cached.temperature,
                feelsLike = cached.temperature,
                tempMin = cached.temperature,
                tempMax = cached.temperature,
                pressure = 0,
                humidity = cached.humidity
            ),
            visibility = 0,
            wind = vcmsa.projects.travelapp.data.model.Wind(cached.windSpeed, 0),
            clouds = vcmsa.projects.travelapp.data.model.Clouds(0),
            dt = cached.cachedAt / 1000,
            sys = vcmsa.projects.travelapp.data.model.Sys(0, 0, "", 0, 0),
            timezone = 0,
            id = 0,
            name = cached.locationName,
            cod = 200
        )
    }
    
    suspend fun clearExpiredCache() {
        val expiryTime = System.currentTimeMillis() - CACHE_EXPIRY_MILLIS
        cachedWeatherDao.deleteOldCachedWeather(expiryTime)
    }
}
