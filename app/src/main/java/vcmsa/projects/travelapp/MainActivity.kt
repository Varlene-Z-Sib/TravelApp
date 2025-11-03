package vcmsa.projects.travelapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import java.util.concurrent.TimeUnit
import vcmsa.projects.travelapp.WeatherWorker

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        sharedPrefs = newBase.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val lang = sharedPrefs.getString("language", "en") ?: "en"

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefs = getSharedPreferences("user_settings", Context.MODE_PRIVATE)

        // Create notification channel
        NotificationUtils.createChannel(this)

        // Only schedule worker if notifications are enabled
        val notificationsEnabled = sharedPrefs.getBoolean("notifications", true)
        if (notificationsEnabled) {
            scheduleWeatherWorker()
            triggerTestNotification() // optional for demo
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_routes -> {
                    replaceFragment(RouteFragment())
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(TripHistoryFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Periodic worker
    fun scheduleWeatherWorker() {
        val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "WeatherCheck",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    // Cancel periodic worker
    fun cancelWeatherWorker() {
        WorkManager.getInstance(this).cancelUniqueWork("WeatherCheck")
    }

    // test notification for demo
    fun triggerTestNotification() {
        val testRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(this).enqueue(testRequest)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
