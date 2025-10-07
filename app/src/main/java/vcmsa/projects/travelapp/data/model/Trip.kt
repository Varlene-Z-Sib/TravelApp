package vcmsa.projects.travelapp.data.model

data class Trip(
    val id: String? = null,
    val destination: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val distanceKm: Double? = null,
    val durationMin: Double? = null,
    val timestamp: Long? = null,
    val city: String? = null,
    val userId: String? = null
)
