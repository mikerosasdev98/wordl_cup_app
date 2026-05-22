package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.database.WorldCupDao
import com.example.data.model.GroupStanding
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.NotificationLogEntity
import com.example.data.model.PlayerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WorldCupRepository(private val dao: WorldCupDao) {

    val allMatchesFlow: Flow<List<MatchEntity>> = dao.getAllMatchesFlow()
    val allPlayersFlow: Flow<List<PlayerEntity>> = dao.getAllPlayersFlow()
    val allNotificationsFlow: Flow<List<NotificationLogEntity>> = dao.getAllNotificationsFlow()

    fun getMatchByIdFlow(id: Int): Flow<MatchEntity?> = dao.getMatchByIdFlow(id)
    fun getEventsForMatchFlow(matchId: Int): Flow<List<MatchEventEntity>> = dao.getEventsForMatchFlow(matchId)

    suspend fun getMatchById(id: Int): MatchEntity? = withContext(Dispatchers.IO) {
        dao.getMatchById(id)
    }

    suspend fun updateMatch(match: MatchEntity) = withContext(Dispatchers.IO) {
        dao.updateMatch(match)
    }

    suspend fun insertEvent(event: MatchEventEntity) = withContext(Dispatchers.IO) {
        dao.insertEvent(event)
    }

    suspend fun deleteEventsForMatch(matchId: Int) = withContext(Dispatchers.IO) {
        dao.deleteEventsForMatch(matchId)
    }

    suspend fun updatePlayer(player: PlayerEntity) = withContext(Dispatchers.IO) {
        dao.updatePlayer(player)
    }

    suspend fun insertPlayers(players: List<PlayerEntity>) = withContext(Dispatchers.IO) {
        dao.insertPlayers(players)
    }

    suspend fun insertNotification(notification: NotificationLogEntity) = withContext(Dispatchers.IO) {
        dao.insertNotification(notification)
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        dao.clearAllNotifications()
    }

    // Helper calculate standings dynamically from a live list of matches
    fun getGroupStandingsMap(matches: List<MatchEntity>): Map<String, List<GroupStanding>> {
        val groupTeams = mapOf(
            "Grupo A" to listOf("México", "EE.UU.", "Canadá", "Costa Rica"),
            "Grupo B" to listOf("Argentina", "Francia", "Marruecos", "Japón"),
            "Grupo C" to listOf("Brasil", "España", "Senegal", "Australia"),
            "Grupo D" to listOf("Inglaterra", "Portugal", "Uruguay", "Ecuador"),
            "Grupo E" to listOf("Alemania", "Bélgica", "Colombia", "Arabia Saudita"),
            "Grupo F" to listOf("Italia", "Croacia", "Camerún", "Corea del Sur"),
            "Grupo G" to listOf("Países Bajos", "Suiza", "Chile", "Irán"),
            "Grupo H" to listOf("Dinamarca", "Nigeria", "Panamá", "Perú"),
            "Grupo I" to listOf("Suecia", "Argelia", "Honduras", "Catar"),
            "Grupo J" to listOf("Ucrania", "Ghana", "Jamaica", "Nueva Zelanda"),
            "Grupo K" to listOf("Serbia", "Egipto", "Costa de Marfil", "Escocia"),
            "Grupo L" to listOf("Austria", "Polonia", "Venezuela", "Túnez")
        )

        val standings = mutableMapOf<String, MutableMap<String, GroupStanding>>()

        // Initialize everyone with 0s
        for ((groupName, teams) in groupTeams) {
            standings[groupName] = mutableMapOf()
            for (team in teams) {
                standings[groupName]!![team] = GroupStanding(
                    team = team, played = 0, won = 0, drawn = 0, lost = 0,
                    goalsFor = 0, goalsAgainst = 0, goalDifference = 0, points = 0
                )
            }
        }

        // Process only finished or live group stage matches
        for (match in matches) {
            val groupPath = match.groupName
            if (groupPath.isEmpty() || !standings.containsKey(groupPath)) continue
            val groupMap = standings[groupPath]!!

            val teamA = match.teamA
            val teamB = match.teamB

            // Confirming team is initialized in group map (pre-seed robustness)
            if (!groupMap.containsKey(teamA)) {
                groupMap[teamA] = GroupStanding(teamA, 0, 0, 0, 0, 0, 0, 0, 0)
            }
            if (!groupMap.containsKey(teamB)) {
                groupMap[teamB] = GroupStanding(teamB, 0, 0, 0, 0, 0, 0, 0, 0)
            }

            if (match.status == "FINISHED" || match.status == "LIVE") {
                val gA = if (match.goalsA >= 0) match.goalsA else 0
                val gB = if (match.goalsB >= 0) match.goalsB else 0

                val sA = groupMap[teamA]!!
                val sB = groupMap[teamB]!!

                val pA = sA.played + 1
                val pB = sB.played + 1

                val wA = sA.won + (if (gA > gB) 1 else 0)
                val wB = sB.won + (if (gB > gA) 1 else 0)

                val dA = sA.drawn + (if (gA == gB) 1 else 0)
                val dB = sB.drawn + (if (gA == gB) 1 else 0)

                val lA = sA.lost + (if (gA < gB) 1 else 0)
                val lB = sB.lost + (if (gB < gA) 1 else 0)

                val gfA = sA.goalsFor + gA
                val gfB = sB.goalsFor + gB

                val gaA = sA.goalsAgainst + gB
                val gaB = sB.goalsAgainst + gA

                val ptsA = sA.points + (if (gA > gB) 3 else if (gA == gB) 1 else 0)
                val ptsB = sB.points + (if (gB > gA) 3 else if (gA == gB) 1 else 0)

                groupMap[teamA] = GroupStanding(
                    team = teamA, played = pA, won = wA, drawn = dA, lost = lA,
                    goalsFor = gfA, goalsAgainst = gaA, goalDifference = gfA - gaA, points = ptsA
                )

                groupMap[teamB] = GroupStanding(
                    team = teamB, played = pB, won = wB, drawn = dB, lost = lB,
                    goalsFor = gfB, goalsAgainst = gaB, goalDifference = gfB - gaB, points = ptsB
                )
            }
        }

        // Sort each list in groups
        return standings.mapValues { (_, teamMap) ->
            teamMap.values.sorted()
        }
    }

    // Seed database with highly realistic starting dataset (matches & players)
    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val count = dao.getMatchesCount()
        if (count > 0) return@withContext // Database already seeded

        Log.d("WorldCupRepository", "Seeding database with World Cup 2026 data...")

        val stadiums = listOf(
            "Estadio Azteca, Cd. de México",
            "MetLife Stadium, Nueva York/NJ",
            "SoFi Stadium, Los Ángeles",
            "BC Place, Vancouver",
            "BMO Field, Toronto",
            "Estadio BBVA, Monterrey",
            "Estadio Akron, Guadalajara",
            "AT&T Stadium, Dallas",
            "Mercedes-Benz Stadium, Atlanta",
            "Hard Rock Stadium, Miami",
            "Arrowhead Stadium, Kansas City",
            "Gillette Stadium, Boston",
            "Lincoln Financial Field, Filadelfia",
            "NRG Stadium, Houston",
            "Lumen Field, Seattle",
            "Levi's Stadium, San Francisco"
        )

        val groupTeams = mapOf(
            "Grupo A" to listOf("México", "EE.UU.", "Canadá", "Costa Rica"),
            "Grupo B" to listOf("Argentina", "Francia", "Marruecos", "Japón"),
            "Grupo C" to listOf("Brasil", "España", "Senegal", "Australia"),
            "Grupo D" to listOf("Inglaterra", "Portugal", "Uruguay", "Ecuador"),
            "Grupo E" to AlemaniaBélgicaColombiaStandard(),
            "Grupo F" to listOf("Italia", "Croacia", "Camerún", "Corea del Sur"),
            "Grupo G" to listOf("Países Bajos", "Suiza", "Chile", "Irán"),
            "Grupo H" to listOf("Dinamarca", "Nigeria", "Panamá", "Perú"),
            "Grupo I" to listOf("Suecia", "Argelia", "Honduras", "Catar"),
            "Grupo J" to listOf("Ucrania", "Ghana", "Jamaica", "Nueva Zelanda"),
            "Grupo K" to listOf("Serbia", "Egipto", "Costa de Marfil", "Escocia"),
            "Grupo L" to listOf("Austria", "Polonia", "Venezuela", "Túnez")
        )

        // Generate complete group stage matches
        val matchesList = mutableListOf<MatchEntity>()
        var idCounter = 1
        val baseDate = 1781136000000L // June 11, 2026 12:00:00 UTC (Approx Copa 2026 start)

        for ((groupName, teams) in groupTeams) {
            val pairings = listOf(
                Pair(0, 1), Pair(2, 3), // Matchday 1
                Pair(0, 2), Pair(1, 3), // Matchday 2
                Pair(0, 3), Pair(1, 2)  // Matchday 3
            )
            val gIdx = groupTeams.keys.indexOf(groupName)

            for (i in pairings.indices) {
                val pair = pairings[i]
                val teamA = teams[pair.first]
                val teamB = teams[pair.second]

                val dayOffset = (gIdx * 2) + (i / 2)
                val hourSelect = if (i % 2 == 0) 15 else 19
                val matchTime = baseDate + (dayOffset * 24 * 60 * 60 * 1000L) + (hourSelect * 60 * 60 * 1000L)
                val stadium = stadiums[(idCounter + gIdx) % stadiums.size]

                // Realism factor: Make Matchday 1 matches already played with realistic exciting scores!
                var goalsA = -1
                var goalsB = -1
                var status = "SCHEDULED"
                if (i == 0 || i == 1) {
                    val outcomes = listOf(
                        Pair(2, 1), Pair(1, 1), Pair(3, 0), Pair(0, 2), Pair(2, 2), Pair(1, 0), Pair(2, 0)
                    )
                    val out = outcomes.random()
                    goalsA = out.first
                    goalsB = out.second
                    status = "FINISHED"
                }

                matchesList.add(
                    MatchEntity(
                        id = idCounter++,
                        teamA = teamA,
                        teamB = teamB,
                        stage = "Fase de Grupos",
                        stadium = stadium,
                        groupName = groupName,
                        matchTimeMillis = matchTime,
                        goalsA = goalsA,
                        goalsB = goalsB,
                        status = status,
                        minute = if (status == "FINISHED") 90 else 0,
                        alertsEnabled = true // Alert enabled by default for host matches & big clashes
                    )
                )
            }
        }

        // Add Knockout Template Matches (Dieciseisavos de Final, 16 partidos)
        // Set match times starting June 28, 2026
        val baseKnockoutDate = baseDate + (17 * 24 * 60 * 60 * 1000L)
        val knockoutStages = listOf(
            Triple("Dieciseisavos de Final", 16, baseKnockoutDate),
            Triple("Octavos de Final", 8, baseKnockoutDate + (5 * 24 * 60 * 60 * 1000L)),
            Triple("Cuartos de Final", 4, baseKnockoutDate + (9 * 24 * 60 * 60 * 1000L)),
            Triple("Semifinal", 2, baseKnockoutDate + (12 * 24 * 60 * 60 * 1000L)),
            Triple("Tercer Puesto", 1, baseKnockoutDate + (14 * 24 * 60 * 60 * 1000L)),
            Triple("Final", 1, baseKnockoutDate + (15 * 24 * 60 * 60 * 1000L))
        )

        var knockoutId = 101
        for ((stageName, qty, baseTime) in knockoutStages) {
            for (m in 0 until qty) {
                val stadium = stadiums[(knockoutId) % stadiums.size]
                val tA = "Ganador P$m"
                val tB = "Segundo P${m + 1}"
                val detailA = if (stageName == "Final") "Ganador Semi 1" else if (stageName == "Dieciseisavos de Final") "Clasificado G${('A' + (m % 12))}" else "Por Determinar"
                val detailB = if (stageName == "Final") "Ganador Semi 2" else if (stageName == "Dieciseisavos de Final") "Clasificado G${('B' + (m % 12))}" else "Por Determinar"

                matchesList.add(
                    MatchEntity(
                        id = knockoutId++,
                        teamA = detailA,
                        teamB = detailB,
                        stage = stageName,
                        stadium = stadium,
                        groupName = "",
                        matchTimeMillis = baseTime + (m * 4 * 60 * 60 * 1000L),
                        goalsA = -1,
                        goalsB = -1,
                        status = "SCHEDULED",
                        minute = 0,
                        alertsEnabled = false
                    )
                )
            }
        }

        dao.insertMatches(matchesList)

        // Seed top world and local players with preliminary Matchday 1 stats
        val seedPlayers = listOf(
            PlayerEntity(name = "Kylian Mbappé", team = "Francia", position = "DEL", goals = 2, assists = 1, matchesPlayed = 1),
            PlayerEntity(name = "Lionel Messi", team = "Argentina", position = "DEL", goals = 1, assists = 1, matchesPlayed = 1),
            PlayerEntity(name = "Santiago Giménez", team = "México", position = "DEL", goals = 2, assists = 0, matchesPlayed = 1),
            PlayerEntity(name = "Christian Pulisic", team = "EE.UU.", position = "DEL", goals = 1, assists = 0, matchesPlayed = 1),
            PlayerEntity(name = "Jude Bellingham", team = "Inglaterra", position = "MED", goals = 1, assists = 1, matchesPlayed = 1),
            PlayerEntity(name = "Vinícius Júnior", team = "Brasil", position = "DEL", goals = 1, assists = 1, matchesPlayed = 1),
            PlayerEntity(name = "Kevin De Bruyne", team = "Bélgica", position = "MED", goals = 0, assists = 2, matchesPlayed = 1),
            PlayerEntity(name = "Robert Lewandowski", team = "Polonia", position = "DEL", goals = 1, assists = 0, matchesPlayed = 1),
            PlayerEntity(name = "Jonathan David", team = "Canadá", position = "DEL", goals = 1, assists = 0, matchesPlayed = 1),
            PlayerEntity(name = "Luis Díaz", team = "Colombia", position = "DEL", goals = 1, assists = 1, matchesPlayed = 1),
            PlayerEntity(name = "Son Heung-min", team = "Corea del Sur", position = "DEL", goals = 1, assists = 0, matchesPlayed = 1),
            PlayerEntity(name = "Federico Valverde", team = "Uruguay", position = "MED", goals = 1, assists = 0, matchesPlayed = 1),
            PlayerEntity(name = "Bruno Fernandes", team = "Portugal", position = "MED", goals = 0, assists = 1, matchesPlayed = 1),
            PlayerEntity(name = "Jamal Musiala", team = "Alemania", position = "MED", goals = 1, assists = 1, matchesPlayed = 1)
        )
        dao.insertPlayers(seedPlayers)

        // Add an initial notification log
        dao.insertNotification(
            NotificationLogEntity(
                matchId = null,
                title = "¡Bienvenidos al Mundial 2026!",
                message = "La Copa Mundial de la FIFA 2026 está lista para comenzar. Revisa el calendario y suscríbete a alertas de tus equipos favoritos."
            )
        )
    }

    private fun AlemaniaBélgicaColombiaStandard(): List<String> = listOf("Alemania", "Bélgica", "Colombia", "Arabia Saudita")
}
