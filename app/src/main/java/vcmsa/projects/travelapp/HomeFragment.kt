package vcmsa.projects.travelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setupNavigationCards(view)

        return view
    }

    private fun setupNavigationCards(view: View) {
        // Route Planning card
        view.findViewById<TextView>(R.id.btnRoutePlanning)?.setOnClickListener {
            navigateToFragment(RouteFragment())
        }

        // Weather Info card
        view.findViewById<TextView>(R.id.btnWeather)?.setOnClickListener {
            navigateToFragment(WeatherFragment())
        }

        // Trip History card
        view.findViewById<TextView>(R.id.btnHistory)?.setOnClickListener {
            navigateToFragment(TripHistoryFragment())
        }

        // Offline Mode card
        view.findViewById<TextView>(R.id.btnOffline)?.setOnClickListener {
            navigateToFragment(OfflineFragment())
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}