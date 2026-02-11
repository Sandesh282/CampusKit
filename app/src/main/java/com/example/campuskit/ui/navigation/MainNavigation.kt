package com.example.campuskit.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.ui.calendar.CalendarScreen
import com.example.campuskit.ui.events.EventsScreen
import com.example.campuskit.ui.home.HomeScreen
import com.example.campuskit.ui.lostfound.LostFoundScreen
import com.example.campuskit.ui.mess.MessScreen
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.NavBarDockBackground
import com.example.campuskit.ui.theme.NavBarUnselected
import com.example.campuskit.ui.theme.SquircleShapeLarge
import com.example.campuskit.ui.theme.TextPrimary

data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val tabs = listOf(
    TabItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
    TabItem("Mess", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    TabItem("Events", Icons.Filled.Event, Icons.Outlined.Event),
    TabItem("Lost", Icons.Filled.SearchOff, Icons.Outlined.SearchOff),
    TabItem("Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
)

@Composable
fun MainNavigation() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
    ) {
        // Screen content with animated transitions
        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (fadeIn(tween(250, easing = FastOutSlowInEasing)) +
                    slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { direction * 40 })
                    .togetherWith(
                        fadeOut(tween(200, easing = FastOutSlowInEasing)) +
                            slideOutHorizontally(tween(250, easing = FastOutSlowInEasing)) { -direction * 40 },
                    )
            },
            label = "tabContent",
        ) { tab ->
            when (tab) {
                0 -> HomeScreen()
                1 -> MessScreen()
                2 -> EventsScreen()
                3 -> LostFoundScreen()
                4 -> CalendarScreen()
            }
        }

        // Custom bottom navigation dock
        CampusKitBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun CampusKitBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        // Thick dock container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = SquircleShapeLarge,
                    ambientColor = Color.Black,
                    spotColor = Color.Black,
                )
                .clip(SquircleShapeLarge)
                .background(NavBarDockBackground)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                DockTabItem(
                    tab = tab,
                    isSelected = index == selectedTab,
                    onClick = { onTabSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun DockTabItem(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f,
        ),
        label = "tabScale",
    )

    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "tabBg",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    AccentBlue.copy(alpha = bgAlpha * 0.2f),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.label,
                tint = if (isSelected) AccentBlue else NavBarUnselected,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = tab.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) TextPrimary else NavBarUnselected,
        )
    }
}
