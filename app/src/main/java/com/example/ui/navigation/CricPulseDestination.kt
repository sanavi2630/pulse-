package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

enum class CricPulseDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    BATTING(
        route = "batting",
        title = "Batting",
        selectedIcon = Icons.Filled.SportsCricket,
        unselectedIcon = Icons.Outlined.SportsCricket,
        testTag = "nav_batting_tab"
    ),
    LIVE_TRAINING(
        route = "training",
        title = "Live Nets",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer,
        testTag = "nav_training_tab"
    ),
    CYCLE_HEALTH(
        route = "cycle",
        title = "Cycle & Body",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange,
        testTag = "nav_cycle_tab"
    ),
    ANALYTICS(
        route = "analytics",
        title = "Correlations",
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics,
        testTag = "nav_analytics_tab"
    )
}
