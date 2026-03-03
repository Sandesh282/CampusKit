package com.example.campuskit.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.campuskit.ui.academic.NotesListScreen
import com.example.campuskit.ui.academic.QuestionPapersListScreen
import com.example.campuskit.ui.calendar.CalendarScreen
import com.example.campuskit.ui.events.EventsScreen
import com.example.campuskit.ui.home.HomeScreen
import com.example.campuskit.ui.lostfound.LostFoundScreen
import com.example.campuskit.ui.mess.MessScreen
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.NavBarDockBackground
import com.example.campuskit.ui.theme.NavBarUnselected
import com.example.campuskit.ui.theme.TextPrimary

data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val tabs = listOf(
    TabItem("Home", Icons.Filled.Cottage, Icons.Outlined.Cottage),
    TabItem("Mess", Icons.Filled.LunchDining, Icons.Outlined.LunchDining),
    TabItem("Events", Icons.Filled.ConfirmationNumber, Icons.Outlined.ConfirmationNumber),
    TabItem("Lost", Icons.Filled.Search, Icons.Outlined.Search),
    TabItem("Calendar", Icons.Filled.CalendarToday, Icons.Outlined.CalendarToday),
)

// Sub-navigation state for screens that slide over Home
sealed class SubScreen {
    data object None : SubScreen()
    data class QuestionPapers(val subjectCode: String) : SubScreen()
    data class Notes(val subjectCode: String) : SubScreen()
}

@Composable
fun MainNavigation() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var subScreen by remember { mutableStateOf<SubScreen>(SubScreen.None) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
    ) {
        // Screen content
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
                0 -> {
                    // Check for active sub-screen
                    when (val current = subScreen) {
                        is SubScreen.QuestionPapers -> QuestionPapersListScreen(
                            subjectCode = current.subjectCode,
                            onBack = { subScreen = SubScreen.None },
                        )
                        is SubScreen.Notes -> NotesListScreen(
                            subjectCode = current.subjectCode,
                            onBack = { subScreen = SubScreen.None },
                        )
                        else -> HomeScreen(
                            onNavigateToQuestionPapers = { code ->
                                subScreen = SubScreen.QuestionPapers(code)
                            },
                            onNavigateToNotes = { code ->
                                subScreen = SubScreen.Notes(code)
                            },
                        )
                    }
                }
                1 -> MessScreen()
                2 -> EventsScreen()
                3 -> LostFoundScreen()
                4 -> CalendarScreen()
            }
        }

        // Reset sub-screen when switching tabs
        if (selectedTab != 0) {
            subScreen = SubScreen.None
        }

        // Bottom nav
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
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black,
                )
                .clip(RoundedCornerShape(28.dp))
                .background(NavBarDockBackground)
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
    // Bouncy scale animation
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "tabScale",
    )

    // Bounce up animation
    val offsetY by animateDpAsState(
        targetValue = if (isSelected) (-2).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "tabOffset",
    )

    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "tabBg",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .offset(y = offsetY)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp),
    ) {
        // Pill-shaped selected indicator
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    AccentBlue.copy(alpha = bgAlpha * 0.15f),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.label,
                tint = if (isSelected) AccentBlue else NavBarUnselected,
                modifier = Modifier.size(22.dp),
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
