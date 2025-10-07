package vcmsa.projects.travelapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vcmsa.projects.travelapp.data.model.RecentSearch

@Dao
interface RecentSearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearch)

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 5")
    suspend fun getRecentSearches(): List<RecentSearch>
}
