package vcmsa.projects.travelapp

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs: SharedPreferences =
            newBase.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"

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

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)

        toolbar.setNavigationOnClickListener {
            // Firebase logout
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

            // Go back to sign-in screen
            val intent = android.content.Intent(this, SignInActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Default fragment when app starts
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
                R.id.nav_history ->{
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

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
