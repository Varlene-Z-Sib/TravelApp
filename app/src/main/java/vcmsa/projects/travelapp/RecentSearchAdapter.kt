package vcmsa.projects.travelapp

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vcmsa.projects.travelapp.data.model.RecentSearch

class RecentSearchAdapter(
    private var searches: List<RecentSearch>,
    private val onItemClick: (RecentSearch) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(search: RecentSearch) {
            textView.text = search.city
            textView.setOnClickListener { onItemClick(search) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context).apply {
            setPadding(32, 24, 32, 24)
            textSize = 16f
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searches[position])
    }

    override fun getItemCount() = searches.size

    fun updateData(newSearches: List<RecentSearch>) {
        searches = newSearches
        notifyDataSetChanged()
    }
}
