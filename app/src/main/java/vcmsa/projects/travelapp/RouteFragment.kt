package vcmsa.projects.travelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vcmsa.projects.travelapp.data.model.Trip

class RouteFragment : Fragment() {

    private lateinit var startPoint: EditText
    private lateinit var endPoint: EditText
    private lateinit var findRouteBtn: Button
    private lateinit var routeResult: TextView
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var recentAdapter: RecentAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val recentSearches = mutableListOf<String>()

    private val apiKey =
        "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6IjdjOGUzMWU0YmE0YjQzMDlhMTU5OGJiNGYzM2M1MzE5IiwiaCI6Im11cm11cjY0In0="

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_route, container, false)

        startPoint = view.findViewById(R.id.startPoint)
        endPoint = view.findViewById(R.id.endPoint)
        findRouteBtn = view.findViewById(R.id.findRouteBtn)
        routeResult = view.findViewById(R.id.routeResult)
        recentRecyclerView = view.findViewById(R.id.recyclerRecents)

        // Setup RecyclerView
        recentAdapter = RecentAdapter(recentSearches)
        recentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentRecyclerView.adapter = recentAdapter

        loadRecents()

        findRouteBtn.setOnClickListener {
            val start = startPoint.text.toString().trim()
            val end = endPoint.text.toString().trim()

            if (start.isNotEmpty() && end.isNotEmpty()) {
                searchCoordinates(start, end)
                saveRecentSearch(start, end)
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_both), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    // Save search (simple string) to Firestore
    private fun saveRecentSearch(start: String, end: String) {
        val userId = auth.currentUser?.uid ?: return
        val search = "$start → $end"
        val data = hashMapOf(
            "search" to search,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("users").document(userId)
            .collection("recentSearches")
            .add(data)
    }

    // Load recents from Firestore
    private fun loadRecents() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("recentSearches")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    recentSearches.clear()
                    for (doc in snapshot.documents) {
                        val search = doc.getString("search")
                        if (search != null) {
                            recentSearches.add(search)
                        }
                    }
                    recentAdapter.notifyDataSetChanged()
                }
            }
    }

    // Convert names → coords
    private fun searchCoordinates(start: String, end: String) {
        RetrofitClient.geoApi.searchLocation(apiKey, start).enqueue(object : Callback<GeoResponse> {
            override fun onResponse(call: Call<GeoResponse>, response: Response<GeoResponse>) {
                val startCoords = response.body()?.features?.firstOrNull()?.geometry?.coordinates
                if (startCoords != null) {
                    val startLonLat = "${startCoords[0]},${startCoords[1]}"

                    RetrofitClient.geoApi.searchLocation(apiKey, end)
                        .enqueue(object : Callback<GeoResponse> {
                            override fun onResponse(
                                call: Call<GeoResponse>,
                                response: Response<GeoResponse>
                            ) {
                                val endCoords =
                                    response.body()?.features?.firstOrNull()?.geometry?.coordinates
                                if (endCoords != null) {
                                    val endLonLat = "${endCoords[0]},${endCoords[1]}"
                                    getRoute(start, end, startLonLat, endLonLat)
                                } else {
                                    routeResult.text = getString(R.string.no_result_end)
                                }
                            }

                            override fun onFailure(call: Call<GeoResponse>, t: Throwable) {
                                routeResult.text = "End lookup failed: ${t.message}"
                            }
                        })
                } else {
                    routeResult.text = getString(R.string.no_result_start)
                }
            }

            override fun onFailure(call: Call<GeoResponse>, t: Throwable) {
                routeResult.text = "Start lookup failed: ${t.message}"
            }
        })
    }

    // Fetch route and save trip
    private fun getRoute(startName: String, endName: String, start: String, end: String) {
        RetrofitClient.routeApi.getRoute(apiKey, start, end)
            .enqueue(object : Callback<RouteResponse> {
                override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                    if (response.isSuccessful) {
                        val summary = response.body()?.features?.firstOrNull()?.properties?.summary
                        summary?.let {
                            val distanceKm = it.distance / 1000.0   // Double
                            val durationMin = it.duration / 60.0    // Double

                            // PASS Doubles (not preformatted Strings) to getString
                            routeResult.text = getString(
                                R.string.route_summary,
                                distanceKm,
                                durationMin
                            )

                            // Save trip (uses data.model.Trip)
                            saveTripResult(startName, endName, distanceKm, durationMin)
                        }
                    } else {
                        routeResult.text = "Error: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                    routeResult.text = "Failed: ${t.message}"
                }
            })
    }

    // Save trip result to Firestore (uses data.model.Trip)
    private fun saveTripResult(
        start: String,
        end: String,
        distanceKm: Double,
        durationMin: Double
    ) {
        val userId = auth.currentUser?.uid ?: return
        val trip = Trip(startDate = start, endDate = end, distanceKm = distanceKm, durationMin = durationMin)

        db.collection("users")
            .document(userId)
            .collection("trips")
            .add(trip)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Trip saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Save failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}

// Simple RecyclerView Adapter
class RecentAdapter(private val items: List<String>) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position]
    }
}
