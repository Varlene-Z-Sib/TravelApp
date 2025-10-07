package vcmsa.projects.travelapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vcmsa.projects.travelapp.data.dao.CachedRouteDao
import vcmsa.projects.travelapp.data.dao.CachedWeatherDao
import vcmsa.projects.travelapp.data.dao.TripDao
import vcmsa.projects.travelapp.data.dao.RecentSearchDao
import vcmsa.projects.travelapp.data.entity.CachedRoute
import vcmsa.projects.travelapp.data.entity.CachedWeather
import vcmsa.projects.travelapp.data.entity.Trip
import vcmsa.projects.travelapp.data.model.RecentSearch

@Database(
    entities = [Trip::class, CachedRoute::class, CachedWeather::class, RecentSearch::class],
    version = 2, // bumped version since we added a new table
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun cachedRouteDao(): CachedRouteDao
    abstract fun cachedWeatherDao(): CachedWeatherDao
    abstract fun recentSearchDao(): RecentSearchDao   // <-- added DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "guide_database"
                )
                    .fallbackToDestructiveMigration() // wipes old DB if schema changed
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
