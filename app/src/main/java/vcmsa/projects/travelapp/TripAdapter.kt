package vcmsa.projects.travelapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vcmsa.projects.travelapp.data.model.Trip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class TripAdapter : ListAdapter<Trip, TripAdapter.TripViewHolder>(DiffCallback()) {

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvRoute: TextView = view.findViewById(R.id.tvRoute)
        private val tvDetails: TextView = view.findViewById(R.id.tvDetails)

        fun bind(trip: Trip) {
            // Your RouteFragment saves startDate and endDate as city names
            val startCity = trip.startDate ?: "Unknown"
            val endCity = trip.endDate ?: "Unknown"
            tvRoute.text = "$startCity → $endCity"

            // Compute distance and duration
            val totalMin = trip.durationMin?.roundToInt() ?: 0
            val hours = totalMin / 60
            val minutes = totalMin % 60
            val distanceStr = String.format(Locale.getDefault(), "%.2f", trip.distanceKm ?: 0.0)

            // Format timestamp if available
            val dateStr = trip.timestamp?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
            } ?: ""

            val detailText = if (dateStr.isNotEmpty())
                "$distanceStr km • ${hours}h ${minutes}m • $dateStr"
            else
                "$distanceStr km • ${hours}h ${minutes}m"

            tvDetails.text = detailText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean =
            oldItem == newItem
    }
}
