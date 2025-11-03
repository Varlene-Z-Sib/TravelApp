package vcmsa.projects.travelapp

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import vcmsa.projects.travelapp.R
import vcmsa.projects.travelapp.data.database.AppDatabase
import vcmsa.projects.travelapp.data.repository.WeatherRepository
import vcmsa.projects.travelapp.network.RetrofitClient
import vcmsa.projects.travelapp.NotificationUtils

class WeatherWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val repo = WeatherRepository(
        RetrofitClient.weatherApi,
        AppDatabase.getDatabase(context).cachedWeatherDao(),
        "12304347a23bca4027937483ec2f7321"
    )

    override suspend fun doWork(): Result {
        val recentSearchDao = AppDatabase.getDatabase(applicationContext).recentSearchDao()
        val lastSearch = recentSearchDao.getRecentSearches().firstOrNull() ?: return Result.success()

        val weather = repo.getWeatherByCity(lastSearch.city) ?: return Result.success()

        val condition = weather.weather.firstOrNull()?.description ?: "Unknown"

        showNotification("Weather Update", "Condition in ${lastSearch.city}: $condition")
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, NotificationUtils.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_travel_logo)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
