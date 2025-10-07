package vcmsa.projects.travelapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import vcmsa.projects.travelapp.data.model.Trip

class TripHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tripAdapter: TripAdapter
    private lateinit var searchInput: EditText
    private lateinit var emptyText: TextView

    private val allTrips = mutableListOf<Trip>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip_history, container, false)

        recyclerView = view.findViewById(R.id.tripHistoryList)
        searchInput = view.findViewById(R.id.searchCityInput)
        emptyText = view.findViewById(R.id.emptyTripsText)

        tripAdapter = TripAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = tripAdapter

        loadTrips()

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTrips(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun loadTrips() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("trips")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                allTrips.clear()
                val trips = documents.mapNotNull { it.toObject(Trip::class.java) }
                allTrips.addAll(trips)
                tripAdapter.submitList(trips)
                emptyText.visibility = if (trips.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                emptyText.text = getString(R.string.failed_load_trips)
                emptyText.visibility = View.VISIBLE
            }
    }

    private fun filterTrips(query: String) {
        val filtered = if (query.isEmpty()) {
            allTrips
        } else {
            allTrips.filter { trip ->
                (trip.startDate?.contains(query, ignoreCase = true) == true) ||
                        (trip.endDate?.contains(query, ignoreCase = true) == true)
            }
        }

        tripAdapter.submitList(filtered)
        emptyText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
}
