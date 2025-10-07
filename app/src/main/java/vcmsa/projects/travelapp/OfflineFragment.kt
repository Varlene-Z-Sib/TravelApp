package vcmsa.projects.travelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class OfflineFragment : Fragment() {

    private lateinit var offlineInfo: TextView
    private lateinit var routesContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_offline, container, false)

        offlineInfo = view.findViewById(R.id.offlineInfo)
        routesContainer = view.findViewById(R.id.routesContainer)

        // Example recent routes (later replace with real stored data)
        val recentRoutes = listOf("📍 Campus ➝ Mall", "📍 Station ➝ Home", "📍 Park Station ➝ Sandton")

        if (recentRoutes.isNotEmpty()) {
            offlineInfo.text = "Recent searches:"

            // Clear old views first
            routesContainer.removeViews(1, routesContainer.childCount - 1)

            // Dynamically create MaterialCardViews for each route
            for (route in recentRoutes) {
                val card = MaterialCardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 16
                    }
                    radius = 24f
                    cardElevation = 6f

                    // Add text inside card
                    addView(TextView(context).apply {
                        text = route
                        textSize = 16f
                        setPadding(32, 32, 32, 32)
                        setTextColor(resources.getColor(android.R.color.black, null))
                    })
                }
                routesContainer.addView(card)
            }
        } else {
            offlineInfo.text = "No offline data available yet."
        }

        return view
    }
}
