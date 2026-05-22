package com.example.ui.screens

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GroupStanding
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.NotificationLogEntity
import com.example.data.model.PlayerEntity
import com.example.ui.viewmodel.WorldCupViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomTopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left section: Menu Icon + App Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "FIFA 2026",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Right section: Notifications + Person Avatar Accents
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alertas",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                // Red dynamic badge dot
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Mi Perfil",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: WorldCupViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val isSeeding by viewModel.isSeeding.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CustomTopHeader()
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant, // #F3EDF7
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "Partidos") },
                    label = { Text("Partidos", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "Posiciones") },
                    label = { Text("Posiciones", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Estadísticas") },
                    label = { Text("Estadísticas", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alertas") },
                    label = { Text("Alertas", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSeeding) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Preparando Fixture del Mundial 2026...",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 15.sp
                    )
                }
            } else {
                when (selectedTab) {
                    0 -> CalendarTab(viewModel)
                    1 -> StandingsTab(viewModel)
                    2 -> StatsTab(viewModel)
                    3 -> AlertasTab(viewModel)
                }
            }
        }
    }
}

// ======================= TAB 1: CALENDARIO INTERACTIVO =======================
@Composable
fun CalendarTab(viewModel: WorldCupViewModel) {
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val simulatingMatchIds by viewModel.simulatingMatchIds.collectAsStateWithLifecycle()

    var stageFilter by remember { mutableStateOf("Fase de Grupos") }
    var selectedMatchForDetail by remember { mutableStateOf<MatchEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Stage filter buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chipColors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant, // #F3EDF7
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant, // #49454F
                selectedContainerColor = MaterialTheme.colorScheme.primary, // #6750A4
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary // White
            )
            val chipBorder = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false,
                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                selectedBorderColor = Color.Transparent
            )
            FilterChip(
                selected = stageFilter == "Fase de Grupos",
                onClick = { stageFilter = "Fase de Grupos" },
                label = { Text("Fase de Grupos", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                colors = chipColors,
                border = chipBorder,
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = stageFilter == "Knockouts",
                onClick = { stageFilter = "Knockouts" },
                label = { Text("Eliminatorias", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                colors = chipColors,
                border = chipBorder,
                modifier = Modifier.weight(1f)
            )
        }

        val filteredMatches = remember(matches, stageFilter) {
            if (stageFilter == "Fase de Grupos") {
                matches.filter { it.stage == "Fase de Grupos" }
            } else {
                matches.filter { it.stage != "Fase de Grupos" }
            }
        }

        if (filteredMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay partidos programados", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredMatches, key = { it.id }) { match ->
                    val isSimulating = simulatingMatchIds.contains(match.id)
                    MatchRowItem(
                        match = match,
                        isSimulating = isSimulating,
                        onRowClick = { selectedMatchForDetail = match },
                        onAlertToggle = { enabled -> viewModel.toggleAlertsForMatch(match.id, enabled) }
                    )
                }
            }
        }
    }

    // Match Detail Dialog
    selectedMatchForDetail?.let { match ->
        // Fetch up-to-date state from current matches list
        val currentMatch = matches.find { it.id == match.id } ?: match
        MatchDetailDialog(
            match = currentMatch,
            viewModel = viewModel,
            onDismiss = { selectedMatchForDetail = null }
        )
    }
}

@Composable
fun MatchRowItem(
    match: MatchEntity,
    isSimulating: Boolean,
    onRowClick: () -> Unit,
    onAlertToggle: (Boolean) -> Unit
) {
    val isLive = match.status == "LIVE"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }
            .testTag("match_item_${match.id}"),
        shape = if (isLive) RoundedCornerShape(28.dp) else RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLive) {
                MaterialTheme.colorScheme.secondaryContainer // #EADDFF
            } else {
                MaterialTheme.colorScheme.surface // #FFFFFF
            }
        ),
        border = if (!isLive) androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ) else null,
        elevation = CardDefaults.cardElevation(if (isLive) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLive) 20.dp else 16.dp)
        ) {
            // Header Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLive) {
                        // En vivo badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(MaterialTheme.colorScheme.secondary) // #21005D
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "En Vivo — ${match.minute}'",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    } else {
                        // Scheduled / Finished status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(
                                    if (match.status == "FINISHED") {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                    } else {
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (match.status == "FINISHED") "FINALIZADO" else "PROGRAMADO",
                                color = if (match.status == "FINISHED") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.75.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isLive) {
                            val shortStadium = match.stadium.substringBefore(",")
                            "${match.groupName} • $shortStadium"
                        } else {
                            match.groupName
                        },
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isLive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Alert bell toggle
                IconButton(
                    onClick = { onAlertToggle(!match.alertsEnabled) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (match.alertsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                        contentDescription = "Notificación",
                        tint = if (match.alertsEnabled) {
                            if (isLive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Scoreboard Row
            if (isLive) {
                // Vertical styled bold layout for en vivo (vertical flags, giant numbers)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Team A
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .shadow(1.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(getTeamFlag(match.teamA), fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = match.teamA.take(3).uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "${match.goalsA}",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            modifier = Modifier.offset(y = (-4).dp)
                        )
                        Text(
                            text = "${match.goalsB}",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Team B
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .shadow(1.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(getTeamFlag(match.teamB), fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = match.teamB.take(3).uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                // Horizontal layout for scheduled and finished matches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Team A
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.teamA,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(getTeamFlag(match.teamA), fontSize = 20.sp)
                        }
                    }

                    // Score or VS
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (match.status == "SCHEDULED") {
                            val simpleTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(match.matchTimeMillis))
                            Text(
                                text = simpleTime,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "${match.goalsA} - ${match.goalsB}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Team B
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(getTeamFlag(match.teamB), fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = match.teamB,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Footer Stadium & Info Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Estadio",
                        tint = if (isLive) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.stadium,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isLive) MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val dateFormatted = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(match.matchTimeMillis))
                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                    color = if (isLive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ======================= POPUP: DETALLE DE MATCH INTERACTIVO =======================
@Composable
fun MatchDetailDialog(
    match: MatchEntity,
    viewModel: WorldCupViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val events by viewModel.getEventsForMatchFlow(match.id).collectAsStateWithLifecycle(emptyList())
    val simulatingMatchIds by viewModel.simulatingMatchIds.collectAsStateWithLifecycle()
    val isSimulating = simulatingMatchIds.contains(match.id)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.stage,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Score Banner inside Popup
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(getTeamFlag(match.teamA), fontSize = 32.sp)
                                Text(match.teamA, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                            }

                            Text(
                                text = if (match.status == "SCHEDULED") "VS" else "${match.goalsA} - ${match.goalsB}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(getTeamFlag(match.teamB), fontSize = 32.sp)
                                Text(match.teamB, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                            }
                        }

                        if (match.status == "LIVE") {
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Partido en curso - Minuto ${match.minute}'",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else if (match.status == "FINISHED") {
                            Text(
                                "Partidos Terminado",
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Time Zone Conversion Panel "calendario interactivo con horarios locales"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QueryBuilder, contentDescription = "Zonas horarias", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Conversor de Horario Mundial", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        val userLocaleTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(match.matchTimeMillis))
                        val stadiumOffset = getStadiumLocalTimeOffset(match.stadium, match.matchTimeMillis)

                        Text("⏰ Tu Hora Local: $userLocaleTime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("🏟️ Sede (${stadiumOffset.first}): ${stadiumOffset.second}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Alerts customization "alertas personalizables"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (match.alertsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "Campana Alerta",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Alertas de Goles y Resúmenes", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Switch(
                        checked = match.alertsEnabled,
                        onCheckedChange = { enabled -> viewModel.toggleAlertsForMatch(match.id, enabled) },
                        modifier = Modifier.testTag("alert_switch_${match.id}")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-Time Simulator Button trigger
                if (match.status != "FINISHED") {
                    Button(
                        onClick = {
                            viewModel.startSimulation(match.id)
                            Toast.makeText(context, "Simulando partido de la copa en vivo...", Toast.LENGTH_SHORT).show()
                        },
                        enabled = !isSimulating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("simulate_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Jugar")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSimulating) "Simulando en Vivo..." else "Simular Partido en Tiempo Real", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Match Events Timeline Scroll
                Text("Sucesos en el Campo (Línea de Tiempo)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                if (events.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (match.status == "SCHEDULED") "Esperando pitazo inicial..." else "Sin eventos reportados",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Box(modifier = Modifier.height(120.dp)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(items = events) { event ->
                                EventRow(event)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventRow(event: MatchEventEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (event.type) {
            "START" -> Icons.Default.Notifications
            "GOAL_A", "GOAL_B" -> Icons.Default.SportsSoccer
            "YELLOW_A", "YELLOW_B" -> Icons.Default.Square
            "RED_A", "RED_B" -> Icons.Default.OfflineBolt
            "HALF" -> Icons.Default.Timer
            else -> Icons.Default.Check
        }

        val iconColor = when (event.type) {
            "GOAL_A", "GOAL_B" -> MaterialTheme.colorScheme.primary
            "YELLOW_A", "YELLOW_B" -> Color(0xFFFFD600)
            "RED_A", "RED_B" -> Color(0xFFD50000)
            else -> MaterialTheme.colorScheme.secondary
        }

        Icon(
            imageVector = icon,
            contentDescription = "Suceso",
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${event.minute}'",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            if (event.playerName.isNotEmpty()) {
                Text(
                    text = event.playerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = event.detail,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}


// ======================= TAB 2: TABLAS DE POSICIONES (GRUPOS A-L) =======================
@Composable
fun StandingsTab(viewModel: WorldCupViewModel) {
    val standings by viewModel.standings.collectAsStateWithLifecycle()
    val groupKeys = remember(standings) { standings.keys.sorted() }
    var selectedGroupKey by remember { mutableStateOf("") }

    // Update default key once loaded
    LaunchedEffect(standings) {
        if (selectedGroupKey.isEmpty() && groupKeys.isNotEmpty()) {
            selectedGroupKey = groupKeys.first()
        }
    }

    if (standings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Horizontal selection carousel of groups A-L
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groupKeys) { groupName ->
                    val isSelected = selectedGroupKey == groupName
                    val chipColors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant, // #F3EDF7
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant, // #49454F
                        selectedContainerColor = MaterialTheme.colorScheme.primary, // #6750A4
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary // White
                    )
                    val chipBorder = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        selectedBorderColor = Color.Transparent
                    )
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGroupKey = groupName },
                        label = { Text(groupName, fontWeight = FontWeight.Bold) },
                        colors = chipColors,
                        border = chipBorder
                    )
                }
            }

            // Display Table for chosen Group
            val groupStandings = standings[selectedGroupKey] ?: emptyList()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Row of Standing Table
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Equipo", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("PJ", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("G", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("E", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("P", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("DG", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Pts", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(groupStandings.size) { index ->
                            val standing = groupStandings[index]
                            StandingRow(standing, rank = index + 1)
                            if (index < groupStandings.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StandingRow(s: GroupStanding, rank: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "$rank",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (rank <= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(18.dp)
            )
            // Flag Emoticon placeholder
            Text(getTeamFlag(s.team), fontSize = 18.sp)
            Spacer(modifier = Modifier.width(6.dp))
            // Name
            Text(
                s.team,
                fontWeight = if (rank <= 2) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Stats Values columns
        Text("${s.played}", modifier = Modifier.weight(0.4f), fontSize = 12.sp, textAlign = TextAlign.Center)
        Text("${s.won}", modifier = Modifier.weight(0.4f), fontSize = 12.sp, textAlign = TextAlign.Center)
        Text("${s.drawn}", modifier = Modifier.weight(0.4f), fontSize = 12.sp, textAlign = TextAlign.Center)
        Text("${s.lost}", modifier = Modifier.weight(0.4f), fontSize = 12.sp, textAlign = TextAlign.Center)
        Text(
            text = (if (s.goalDifference > 0) "+" else "") + "${s.goalDifference}",
            modifier = Modifier.weight(0.5f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = if (s.goalDifference > 0) MaterialTheme.colorScheme.primary else if (s.goalDifference < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${s.points}",
            modifier = Modifier.weight(0.5f),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


// ======================= TAB 3: ESTADÍSTICAS DETALLADAS =======================
@Composable
fun StatsTab(viewModel: WorldCupViewModel) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    var statsCategory by remember { mutableStateOf("Goles") } // "Goles", "Asistencias"

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle categories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chipColors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant, // #F3EDF7
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant, // #49454F
                selectedContainerColor = MaterialTheme.colorScheme.primary, // #6750A4
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary // White
            )
            val chipBorder = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false,
                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                selectedBorderColor = Color.Transparent
            )
            FilterChip(
                selected = statsCategory == "Goles",
                onClick = { statsCategory = "Goles" },
                label = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SportsSoccer, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Goleadores", fontWeight = FontWeight.Bold)
                    }
                },
                colors = chipColors,
                border = chipBorder,
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = statsCategory == "Asistencias",
                onClick = { statsCategory = "Asistencias" },
                label = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Asistencias", fontWeight = FontWeight.Bold)
                    }
                },
                colors = chipColors,
                border = chipBorder,
                modifier = Modifier.weight(1f)
            )
        }

        val sortedList = remember(players, statsCategory) {
            if (statsCategory == "Goles") {
                players.sortedByDescending { it.goals }
            } else {
                players.sortedByDescending { it.assists }
            }
        }

        if (sortedList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando lista de jugadores...", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedList.size) { index ->
                    val p = sortedList[index]
                    PlayerStatCard(player = p, rank = index + 1, highlightCategory = statsCategory)
                }
            }
        }
    }
}

@Composable
fun PlayerStatCard(player: PlayerEntity, rank: Int, highlightCategory: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge - Design matching #EADDFF and #21005D podium styles
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (rank <= 3) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = if (rank <= 3) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Player details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(getTeamFlag(player.team), fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${player.team} • ${player.position}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stat Value output circle with high weight matching HTML
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer) // #EADDFF
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                val value = if (highlightCategory == "Goles") player.goals else player.assists
                Text(
                    text = "$value " + if (highlightCategory == "Goles") "G" else "A",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.secondary // #21005D
                )
            }
        }
    }
}


// ======================= TAB 4: CENTRAL DE ALERTAS / PUSH LOGS =======================
@Composable
fun AlertasTab(viewModel: WorldCupViewModel) {
    val context = LocalContext.current
    val notificationsLog by viewModel.notificationsLog.collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Upper Controls card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Central de Alertas del Mundial",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Esta consola simula el sistema de notificaciones 'push' contratado para goles de cada encuentro y resúmenes de cierre.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.generateNotificationTest()
                            Toast.makeText(context, "Alerta instantánea de gol enviada", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Forzar Gol", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearAllNotificationCenter() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Limpiar Todo", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notifications List Logs
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Historial de Alertas Push Recibidas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                "Total: ${notificationsLog.size}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (notificationsLog.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Sin notificaciones acumuladas",
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notificationsLog, key = { it.id }) { alertLog ->
                    AlertRowItem(alertLog)
                }
            }
        }
    }
}

@Composable
fun AlertRowItem(alert: NotificationLogEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = alert.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Time readable format
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(alert.timestamp))
                Text(timeStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.message,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}


// ======================= DATA STATIC HELPERS (FLAGS ACCURACY) =======================
fun getTeamFlag(teamName: String): String {
    val flags = mapOf(
        "México" to "🇲🇽",
        "EE.UU." to "🇺🇸",
        "Canadá" to "🇨🇦",
        "Costa Rica" to "🇨🇷",
        "Argentina" to "🇦🇷",
        "Francia" to "🇫🇷",
        "Marruecos" to "🇲🇦",
        "Japón" to "🇯🇵",
        "Brasil" to "🇧🇷",
        "España" to "🇪🇸",
        "Senegal" to "🇸🇳",
        "Australia" to "🇦🇺",
        "Inglaterra" to "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
        "Portugal" to "🇵🇹",
        "Uruguay" to "🇺🇾",
        "Ecuador" to "🇪🇨",
        "Alemania" to "🇩🇪",
        "Bélgica" to "🇧🇪",
        "Colombia" to "🇨🇴",
        "Arabia Saudita" to "🇸🇦",
        "Italia" to "🇮🇹",
        "Croacia" to "🇭🇷",
        "Camerún" to "🇨🇲",
        "Corea del Sur" to "🇰🇷",
        "Países Bajos" to "🇳🇱",
        "Suiza" to "🇨🇭",
        "Chile" to "🇨🇱",
        "Irán" to "🇮🇷",
        "Dinamarca" to "🇩🇰",
        "Nigeria" to "🇳🇬",
        "Panamá" to "🇵🇦",
        "Perú" to "🇵🇪",
        "Suecia" to "🇸🇪",
        "Argelia" to "🇩🇿",
        "Honduras" to "🇭🇳",
        "Catar" to "🇶🇦",
        "Ucrania" to "🇺🇦",
        "Ghana" to "🇬🇭",
        "Jamaica" to "🇯🇲",
        "Nueva Zelanda" to "🇳🇿",
        "Serbia" to "🇷🇸",
        "Egipto" to "🇪🇬",
        "Costa de Marfil" to "🇨🇮",
        "Escocia" to "🏴󠁧󠁢󠁳󠁣󠁴󠁿",
        "Austria" to "🇦🇹",
        "Polonia" to "🇵🇱",
        "Venezuela" to "🇻🇪",
        "Túnez" to "🇹🇳"
    )
    return flags[teamName] ?: "🏳️"
}

// Convert stadium to timezone offsets (Pacífico PT, Centro CT, Este ET)
fun getStadiumLocalTimeOffset(stadium: String, timestamp: Long): Pair<String, String> {
    val sd = stadium.lowercase()
    val zone = when {
        sd.contains("vancouver") || sd.contains("los ángeles") || sd.contains("san francisco") || sd.contains("seattle") -> {
            TimeZone.getTimeZone("America/Los_Angeles")
        }
        sd.contains("cd. de méxico") || sd.contains("monterrey") || sd.contains("guadalajara") || sd.contains("dallas") || sd.contains("houston") || sd.contains("kansas city") -> {
            TimeZone.getTimeZone("America/Mexico_City")
        }
        else -> {
            TimeZone.getTimeZone("America/New_York")
        }
    }

    val sDate = Date(timestamp)
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
        timeZone = zone
    }

    val offsetName = when (zone.id) {
        "America/Los_Angeles" -> "PDT / UTC-7"
        "America/Mexico_City" -> "CDT / UTC-5"
        else -> "EDT / UTC-4"
    }

    return Pair(offsetName, sdf.format(sDate))
}
