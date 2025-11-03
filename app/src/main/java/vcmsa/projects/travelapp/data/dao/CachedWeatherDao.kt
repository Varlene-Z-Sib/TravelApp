package vcmsa.projects.travelapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vcmsa.projects.travelapp.data.entity.CachedWeather

@Dao
interface CachedWeatherDao {
    
    @Query("SELECT * FROM cached_weather WHERE latitude = :lat AND longitude = :lng AND forecastType = :type ORDER BY cachedAt DESC LIMIT 1")
    suspend fun getCachedWeather(lat: Double, lng: Double, type: String = "current"): CachedWeather?
    
    @Query("SELECT * FROM cached_weather ORDER BY cachedAt DESC LIMIT :limit")
    fun getRecentCachedWeather(limit: Int = 10): Flow<List<CachedWeather>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(weather: CachedWeather): Long

    @Query("SELECT * FROM cached_weather WHERE locationName = :cityName ORDER BY cachedAt DESC LIMIT 1")
    suspend fun getCachedWeatherByName(cityName: String): CachedWeather?


    @Delete
    suspend fun deleteCachedWeather(weather: CachedWeather)
    
    @Query("DELETE FROM cached_weather WHERE cachedAt < :timestamp")
    suspend fun deleteOldCachedWeather(timestamp: Long)
    
    @Query("DELETE FROM cached_weather")
    suspend fun deleteAllCachedWeather()
}
