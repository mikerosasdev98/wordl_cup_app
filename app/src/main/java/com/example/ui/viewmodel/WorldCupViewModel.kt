package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.GroupStanding
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.NotificationLogEntity
import com.example.data.model.PlayerEntity
import com.example.data.repository.WorldCupRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorldCupViewModel(
    private val repository: WorldCupRepository,
    private val context: Context
) : ViewModel() {

    // Seeding trigger state
    private val _isSeeding = MutableStateFlow(true)
    val isSeeding: StateFlow<Boolean> = _isSeeding.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            _isSeeding.value = false
        }
    }

    // Matches
    val matches: StateFlow<List<MatchEntity>> = repository.allMatchesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Players
    val players: StateFlow<List<PlayerEntity>> = repository.allPlayersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Historical Notifications log
    val notificationsLog: StateFlow<List<NotificationLogEntity>> = repository.allNotificationsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Dynamic Group Standings calculated from finished/live matches
    val standings: StateFlow<Map<String, List<GroupStanding>>> = matches
        .combine(_isSeeding) { matchList, seeding ->
            if (seeding) emptyMap() else repository.getGroupStandingsMap(matchList)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Track active simulation coroutine jobs
    private val activeSimulations = mutableMapOf<Int, Job>()

    // Exposed Live Match States mapping for screen tracking
    private val _simulatingMatchIds = MutableStateFlow<Set<Int>>(emptySet())
    val simulatingMatchIds: StateFlow<Set<Int>> = _simulatingMatchIds.asStateFlow()

    fun getEventsForMatchFlow(matchId: Int): Flow<List<MatchEventEntity>> {
        return repository.getEventsForMatchFlow(matchId)
    }

    // Toggle match notifications/alerts "alertas personalizables"
    fun toggleAlertsForMatch(matchId: Int, enabled: Boolean) {
        viewModelScope.launch {
            val match = repository.getMatchById(matchId) ?: return@launch
            repository.updateMatch(match.copy(alertsEnabled = enabled))
        }
    }

    // Toggle subscription
    fun toggleSubscripcionMatch(matchId: Int, enabled: Boolean) {
        viewModelScope.launch {
            val match = repository.getMatchById(matchId) ?: return@launch
            repository.updateMatch(match.copy(subscribed = enabled))
        }
    }

    // Delete all logs
    fun clearAllNotificationCenter() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    // Start Real-Time Simulation for a Match
    fun startSimulation(matchId: Int) {
        if (activeSimulations.containsKey(matchId)) return // Already simulating

        val job = viewModelScope.launch {
            _simulatingMatchIds.value = _simulatingMatchIds.value + matchId

            // Read latest match
            val initialMatch = repository.getMatchById(matchId) ?: return@launch
            repository.deleteEventsForMatch(matchId) // Clean old preview events if any

            // Mark Match as Live and Reset score to 0-0
            var currentMatch = initialMatch.copy(
                status = "LIVE",
                goalsA = 0,
                goalsB = 0,
                minute = 1
            )
            repository.updateMatch(currentMatch)

            // Log Inicio
            repository.insertEvent(
                MatchEventEntity(
                    matchId = matchId,
                    type = "START",
                    minute = 1,
                    playerName = "",
                    detail = "¡Comienza el partido en el ${currentMatch.stadium}!"
                )
            )

            if (currentMatch.alertsEnabled) {
                NotificationHelper.sendNotification(
                    context,
                    matchId * 10,
                    "¡Partido en Vivo! ⚽",
                    "Ha comenzado el encuentro: ${currentMatch.teamA} vs ${currentMatch.teamB}."
                )
                repository.insertNotification(
                    NotificationLogEntity(
                        matchId = matchId,
                        title = "¡Kickoff!",
                        message = "Inició el partido ${currentMatch.teamA} vs ${currentMatch.teamB}."
                    )
                )
            }

            // Simulate minutes 1 to 90
            for (min in 2..90) {
                delay(800) // Fast simulation: 800ms per minute. Full match takes ~72 seconds

                // Minute update
                currentMatch = currentMatch.copy(minute = min)

                // Half-time Check
                if (min == 45) {
                    repository.insertEvent(
                        MatchEventEntity(
                            matchId = matchId,
                            type = "HALF",
                            minute = 45,
                            playerName = "",
                            detail = "Medio Tiempo: ${currentMatch.teamA} ${currentMatch.goalsA} - ${currentMatch.goalsB} ${currentMatch.teamB}"
                        )
                    )
                    if (currentMatch.alertsEnabled) {
                        NotificationHelper.sendNotification(
                            context,
                            matchId * 10 + 1,
                            "Medio Tiempo 🌟",
                            "${currentMatch.teamA} ${currentMatch.goalsA} - ${currentMatch.goalsB} ${currentMatch.teamB}."
                        )
                    }
                }

                // Chance to score or event (approx 6% chance per minute)
                val chance = (1..100).random()
                if (chance <= 6) {
                    val isTeamA = (1..2).random() == 1
                    val scorerGoal = (1..100).random() <= 75 // 75% goal, 25% card/event

                    if (scorerGoal) {
                        // GOAL!
                        val goalText = listOf(
                            "Remate de pierna derecha tras un centro raso",
                            "Espectacular tiro libre directo al ángulo",
                            "Cabezazo letal tras un cobro de tiro de esquina",
                            "Definición magistral en el área chica"
                        ).random()

                        if (isTeamA) {
                            currentMatch = currentMatch.copy(goalsA = currentMatch.goalsA + 1)
                            val scorer = getRandomPlayerOfTeam(currentMatch.teamA)
                            repository.insertEvent(
                                MatchEventEntity(
                                    matchId = matchId,
                                    type = "GOAL_A",
                                    minute = min,
                                    playerName = scorer,
                                    detail = "GOL de ${currentMatch.teamA}"
                                )
                            )
                            incrementPlayerGoals(scorer, currentMatch.teamA)

                            if (currentMatch.alertsEnabled) {
                                NotificationHelper.sendNotification(
                                    context,
                                    matchId * 100 + min,
                                    "⚽ ¡GOOOL de ${currentMatch.teamA}! (${currentMatch.goalsA}-${currentMatch.goalsB})",
                                    "${min}' - $scorer firmó un golazo. $goalText."
                                )
                                repository.insertNotification(
                                    NotificationLogEntity(
                                        matchId = matchId,
                                        title = "⚽ GOL de ${currentMatch.teamA}",
                                        message = "Al minuto ${min}', $scorer anotó el ${currentMatch.goalsA}-${currentMatch.goalsB}."
                                    )
                                )
                            }
                        } else {
                            currentMatch = currentMatch.copy(goalsB = currentMatch.goalsB + 1)
                            val scorer = getRandomPlayerOfTeam(currentMatch.teamB)
                            repository.insertEvent(
                                MatchEventEntity(
                                    matchId = matchId,
                                    type = "GOAL_B",
                                    minute = min,
                                    playerName = scorer,
                                    detail = "GOL de ${currentMatch.teamB}"
                                )
                            )
                            incrementPlayerGoals(scorer, currentMatch.teamB)

                            if (currentMatch.alertsEnabled) {
                                NotificationHelper.sendNotification(
                                    context,
                                    matchId * 100 + min,
                                    "⚽ ¡GOOOL de ${currentMatch.teamB}! (${currentMatch.goalsA}-${currentMatch.goalsB})",
                                    "${min}' - $scorer firmó un golazo. $goalText."
                                )
                                repository.insertNotification(
                                    NotificationLogEntity(
                                        matchId = matchId,
                                        title = "⚽ GOL de ${currentMatch.teamB}",
                                        message = "Al minuto ${min}', $scorer anotó el ${currentMatch.goalsA}-${currentMatch.goalsB}."
                                    )
                                )
                            }
                        }
                    } else {
                        // Yellow/Red Cards
                        val isYellow = (1..100).random() <= 85
                        val cardedPlayer = getRandomPlayerOfTeam(if (isTeamA) currentMatch.teamA else currentMatch.teamB)

                        if (isYellow) {
                            repository.insertEvent(
                                MatchEventEntity(
                                    matchId = matchId,
                                    type = if (isTeamA) "YELLOW_A" else "YELLOW_B",
                                    minute = min,
                                    playerName = cardedPlayer,
                                    detail = "Tarjeta Amarilla"
                                )
                            )
                            incrementPlayerCards(cardedPlayer, isYellow = true)
                        } else {
                            repository.insertEvent(
                                MatchEventEntity(
                                    matchId = matchId,
                                    type = if (isTeamA) "RED_A" else "RED_B",
                                    minute = min,
                                    playerName = cardedPlayer,
                                    detail = "Tarjeta Roja"
                                )
                            )
                            incrementPlayerCards(cardedPlayer, isYellow = false)

                            if (currentMatch.alertsEnabled) {
                                NotificationHelper.sendNotification(
                                    context,
                                    matchId * 100 + min,
                                    "🔴 Tarjeta Roja para ${if (isTeamA) currentMatch.teamA else currentMatch.teamB}",
                                    "${min}' - El árbitro expulsa a $cardedPlayer por una dura entrada."
                                )
                            }
                        }
                    }
                }

                repository.updateMatch(currentMatch)
            }

            // Finish match
            currentMatch = currentMatch.copy(status = "FINISHED")
            repository.updateMatch(currentMatch)

            // Final Events
            val matchSummaries = listOf(
                "Un emocionante partido que mantiene a la fanaticada de pie.",
                "Partido táctico y muy disputado en todas las zonas del campo.",
                "Increíble demostración de fútbol que define posiciones clave.",
                "Un clásico instantáneo que será recordado en la historia del mundial."
            ).random()

            repository.insertEvent(
                MatchEventEntity(
                    matchId = matchId,
                    type = "END",
                    minute = 90,
                    playerName = "",
                    detail = "¡Final del Partido! Marcador Final: ${currentMatch.teamA} ${currentMatch.goalsA} - ${currentMatch.goalsB} ${currentMatch.teamB}"
                )
            )

            // Trigger Final Summary Notifications "notificaciones push al finalizar cada partido"
            if (currentMatch.alertsEnabled) {
                val notificationTitle = "🏁 Resultado Final: ${currentMatch.teamA} ${currentMatch.goalsA} - ${currentMatch.goalsB} ${currentMatch.teamB}"
                val notificationBody = "Fin de los 90'. Resumen: $matchSummaries"

                NotificationHelper.sendNotification(
                    context,
                    matchId * 10 + 2,
                    notificationTitle,
                    notificationBody
                )

                repository.insertNotification(
                    NotificationLogEntity(
                        matchId = matchId,
                        title = "🏁 Partido Finalizado",
                        message = "Resultado: ${currentMatch.teamA} ${currentMatch.goalsA} - ${currentMatch.goalsB} ${currentMatch.teamB}. $matchSummaries"
                    )
                )
            }

            // Clean-up simulating lists
            activeSimulations.remove(matchId)
            _simulatingMatchIds.value = _simulatingMatchIds.value - matchId
        }

        activeSimulations[matchId] = job
    }

    // Generate random notification for a generic test match
    fun generateNotificationTest() {
        viewModelScope.launch {
            val teams = listOf("México", "EE.UU.", "Argentina", "Brasil", "Francia", "España", "Alemania", "Inglaterra")
            val t1 = teams.random()
            val t2 = teams.filter { it != t1 }.random()
            val minute = (1..90).random()
            val title = "⚽ ¡GOL Sorpresa del Mundial!"
            val message = "Minuto ${minute}': ¡GOL de $t1 ante $t2! El marcador se actualiza."

            NotificationHelper.sendNotification(context, (1000..9999).random(), title, message)
            repository.insertNotification(
                NotificationLogEntity(
                    matchId = null,
                    title = title,
                    message = message
                )
            )
        }
    }

    // Helper statistics random updates
    private suspend fun incrementPlayerGoals(playerName: String, team: String) {
        val allPlayers = players.value
        val exPlayer = allPlayers.find { it.name == playerName }
        if (exPlayer != null) {
            repository.updatePlayer(exPlayer.copy(goals = exPlayer.goals + 1))
        } else {
            // Unsorted/other scorer - insert a temporary new player
            val newP = PlayerEntity(
                name = playerName, team = team, position = "DEL", goals = 1, assists = 0, matchesPlayed = 1
            )
            repository.insertPlayers(listOf(newP))
        }
    }

    private suspend fun incrementPlayerCards(playerName: String, isYellow: Boolean) {
        val allPlayers = players.value
        val exPlayer = allPlayers.find { it.name == playerName } ?: return
        if (isYellow) {
            repository.updatePlayer(exPlayer.copy(yellowCards = exPlayer.yellowCards + 1))
        } else {
            repository.updatePlayer(exPlayer.copy(redCards = exPlayer.redCards + 1))
        }
    }

    // Assign realistic scorers dynamically
    private fun getRandomPlayerOfTeam(team: String): String {
        val presetPlayers = mapOf(
            "Argentina" to listOf("Lionel Messi", "Lautaro Martínez", "Julián Álvarez", "Rodrigo De Paul"),
            "Francia" to listOf("Kylian Mbappé", "Antoine Griezmann", "Olivier Giroud", "Ousmane Dembélé"),
            "México" to listOf("Santiago Giménez", "Hirving Lozano", "Orbelín Pineda", "Edson Álvarez"),
            "EE.UU." to listOf("Christian Pulisic", "Folarin Balogun", "Timothy Weah", "Weston McKennie"),
            "Canadá" to listOf("Jonathan David", "Alphonso Davies", "Cyle Larin", "Tajon Buchanan"),
            "Inglaterra" to listOf("Harry Kane", "Bukayo Saka", "Jude Bellingham", "Phil Foden"),
            "Brasil" to listOf("Vinícius Júnior", "Rodrygo", "Richarlison", "Lucas Paquetá"),
            "España" to listOf("Álvaro Morata", "Lamine Yamal", "Nico Williams", "Dani Olmo"),
            "Bélgica" to listOf("Romelu Lukaku", "Kevin De Bruyne", "Leandro Trossard", "Jeremy Doku"),
            "Colombia" to listOf("Luis Díaz", "James Rodríguez", "Rafael Borré", "Jhon Durán"),
            "Alemania" to listOf("Kai Havertz", "Jamal Musiala", "Florian Wirtz", "Leroy Sané"),
            "Uruguay" to listOf("Darwin Núñez", "Federico Valverde", "Facundo Pellistri", "Rodrigo Bentancur"),
            "Italia" to listOf("Mateo Retegui", "Federico Chiesa", "Giacomo Raspadori", "Nicolò Barella"),
            "Croacia" to listOf("Andrej Kramarić", "Luka Modrić", "Ivan Perišić", "Mateo Kovačić"),
            "Países Bajos" to listOf("Memphis Depay", "Cody Gakpo", "Xavi Simons", "Denzel Dumfries")
        )

        val names = presetPlayers[team] ?: listOf("A. Goleador", "M. Delantero", "J. Mediocampista", "P. Estrella")
        return names.random()
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel all active simulations upon destroy
        activeSimulations.values.forEach { it.cancel() }
        activeSimulations.clear()
    }
}

class WorldCupViewModelFactory(
    private val repository: WorldCupRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorldCupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorldCupViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ya")
    }
}
