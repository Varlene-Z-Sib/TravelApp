package vcmsa.projects.travelapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var switchDarkMode: Switch
    private lateinit var switchNotifications: Switch
    private lateinit var spinnerLanguage: Spinner
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var sharedPrefs: SharedPreferences

    private var isSpinnerInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPrefs = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        tvName = view.findViewById(R.id.tvUserName)
        tvEmail = view.findViewById(R.id.tvUserEmail)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage)

        loadUserProfile()
        loadSettings()
        setupListeners()

        return view
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        user?.let {
            tvName.text = it.displayName ?: "Guest User"
            tvEmail.text = it.email ?: "No Email"
        }
    }

    private fun loadSettings() {
        val darkModeEnabled = sharedPrefs.getBoolean("dark_mode", false)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications", true)
        val selectedLanguage = sharedPrefs.getString("language", "en")

        switchDarkMode.isChecked = darkModeEnabled
        switchNotifications.isChecked = notificationsEnabled

        // updated to handle Afrikaans
        val langIndex = when (selectedLanguage) {
            "zu" -> 1
            "af" -> 2
            else -> 0
        }

        spinnerLanguage.setSelection(langIndex)
    }

    private fun setupListeners() {
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("notifications", isChecked).apply()
            val mainActivity = requireActivity() as MainActivity

            if (isChecked) {
                mainActivity.scheduleWeatherWorker()
                mainActivity.triggerTestNotification()
                Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                mainActivity.cancelWeatherWorker()
                Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true
                    return
                }

                // Updated for Afrikaans
                val selectedLang = when (position) {
                    1 -> "zu"
                    2 -> "af"
                    else -> "en"
                }

                val currentLang = sharedPrefs.getString("language", "en")
                if (currentLang != selectedLang) {
                    sharedPrefs.edit().putString("language", selectedLang).apply()
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
