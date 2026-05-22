package com.example.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.NotificationLogEntity
import com.example.data.model.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldCupDao {
    @Query("SELECT * FROM matches ORDER BY matchTimeMillis ASC")
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id")
    fun getMatchByIdFlow(id: Int): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: Int): MatchEntity?

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun getMatchesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("SELECT * FROM match_events WHERE matchId = :matchId ORDER BY minute ASC, id ASC")
    fun getEventsForMatchFlow(matchId: Int): Flow<List<MatchEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: MatchEventEntity)

    @Query("DELETE FROM match_events WHERE matchId = :matchId")
    suspend fun deleteEventsForMatch(matchId: Int)

    @Query("SELECT * FROM players ORDER BY goals DESC, assists DESC, name ASC")
    fun getAllPlayersFlow(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLogEntity)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}

@Database(
    entities = [
        MatchEntity::class,
        MatchEventEntity::class,
        PlayerEntity::class,
        NotificationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WorldCupDatabase : RoomDatabase() {
    abstract fun worldCupDao(): WorldCupDao

    companion object {
        @Volatile
        private var INSTANCE: WorldCupDatabase? = null

        fun getDatabase(context: Context): WorldCupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorldCupDatabase::class.java,
                    "world_cup_2026_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
