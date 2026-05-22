package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: Int,
    val teamA: String,
    val teamB: String,
    val stage: String, // "Fase de Grupos", "Dieciseisavos", "Octavos", "Cuartos", "Semifinal", "Tercer Puesto", "Final"
    val stadium: String,
    val groupName: String, // "Grupo A", "Grupo B", etc.
    val matchTimeMillis: Long, // timestamp
    val goalsA: Int, // -1 if not played yet
    val goalsB: Int, // -1 if not played yet
    val status: String, // "SCHEDULED", "LIVE", "FINISHED"
    val minute: Int = 0, // 0 to 90
    val alertsEnabled: Boolean = false, // Goal notifications active for this match
    val subscribed: Boolean = false // Track match state
)

@Entity(tableName = "match_events")
data class MatchEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: Int,
    val type: String, // "GOAL_A", "GOAL_B", "YELLOW_A", "YELLOW_B", "RED_A", "RED_B", "START", "HALF", "END"
    val minute: Int,
    val playerName: String,
    val detail: String // "GOL", "Tarjeta Amarilla", etc.
)

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val team: String,
    val position: String, // "DEL", "MED", "DEF", "POR"
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val matchesPlayed: Int = 0
)

@Entity(tableName = "notifications")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: Int?,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// UI Domain models for positions calculations
data class GroupStanding(
    val team: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int
) : Comparable<GroupStanding> {
    override fun compareTo(other: GroupStanding): Int {
        // Compare points first
        if (this.points != other.points) {
            return other.points.compareTo(this.points)
        }
        // Then compare goal difference
        if (this.goalDifference != other.goalDifference) {
            return other.goalDifference.compareTo(this.goalDifference)
        }
        // Then goals for
        if (this.goalsFor != other.goalsFor) {
            return other.goalsFor.compareTo(this.goalsFor)
        }
        // Finally team name
        return this.team.compareTo(other.team)
    }
}
