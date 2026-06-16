package com.example.campuskit.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.ui.academic.NotesListScreen
import com.example.campuskit.ui.academic.PdfViewerScreen
import com.example.campuskit.ui.academic.QuestionPapersListScreen
import com.example.campuskit.ui.assistant.AssistantContent
import com.example.campuskit.ui.assistant.AssistantViewModel
import com.example.campuskit.ui.calendar.CalendarScreen
import com.example.campuskit.ui.events.EventsScreen
import com.example.campuskit.ui.home.HomeScreen
import com.example.campuskit.ui.lostfound.LostFoundScreen
import com.example.campuskit.ui.mess.MessScreen
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AccentPurple
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.NavBarDockBackground
import com.example.campuskit.ui.theme.NavBarUnselected
import com.example.campuskit.ui.theme.TextPrimary
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

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
    data class PdfViewer(val url: String, val title: String) : SubScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var subScreenStack by remember { mutableStateOf<List<SubScreen>>(emptyList()) }
    var showAssistant by remember { mutableStateOf(false) }
    val assistantSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val currentSubScreen = subScreenStack.lastOrNull() ?: SubScreen.None

    // Hide bubble when user is in a full-screen sub-navigation (QP, Notes, PDF)
    val showBubble = subScreenStack.isEmpty()

    fun push(screen: SubScreen) {
        subScreenStack = subScreenStack + screen
    }

    fun pop() {
        if (subScreenStack.isNotEmpty()) {
            subScreenStack = subScreenStack.dropLast(1)
        }
    }

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
                    AnimatedContent(
                        targetState = currentSubScreen,
                        transitionSpec = {
                            if (targetState != SubScreen.None && initialState == SubScreen.None) {
                                (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it })
                                    .togetherWith(fadeOut(tween(300)))
                            } else if (targetState == SubScreen.None && initialState != SubScreen.None) {
                                fadeIn(tween(300))
                                    .togetherWith(slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it })
                            } else if (targetState is SubScreen.PdfViewer) {
                                (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it })
                                    .togetherWith(slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 3 })
                            } else {
                                (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 3 })
                                    .togetherWith(slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it })
                            }
                        },
                        label = "subScreenTransition"
                    ) { screen ->
                        when (screen) {
                            is SubScreen.QuestionPapers -> QuestionPapersListScreen(
                                subjectCode = screen.subjectCode,
                                onBack = { pop() },
                                onOpenPdf = { url, title -> push(SubScreen.PdfViewer(url, title)) }
                            )
                            is SubScreen.Notes -> NotesListScreen(
                                subjectCode = screen.subjectCode,
                                onBack = { pop() },
                                onOpenPdf = { url, title -> push(SubScreen.PdfViewer(url, title)) }
                            )
                            is SubScreen.PdfViewer -> PdfViewerScreen(
                                url = screen.url,
                                title = screen.title,
                                onBack = { pop() }
                            )
                            SubScreen.None -> HomeScreen(
                                onNavigateToQuestionPapers = { code -> push(SubScreen.QuestionPapers(code)) },
                                onNavigateToNotes = { code -> push(SubScreen.Notes(code)) },
                            )
                        }
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
            subScreenStack = emptyList()
        }

        // Bottom nav
        CampusKitBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        // AI floating bubble — bottom-left, above nav bar, hidden on sub-screens
        AnimatedVisibility(
            visible = showBubble,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 104.dp)
                .navigationBarsPadding(),
            enter = scaleIn(spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut(tween(150)) + fadeOut(tween(150)),
        ) {
            AiFloatingBubble(onClick = { showAssistant = true })
        }
    }

    // Assistant bottom sheet
    if (showAssistant) {
        val assistantViewModel: AssistantViewModel = hiltViewModel()
        val uiState by assistantViewModel.uiState.collectAsStateWithLifecycle()

        ModalBottomSheet(
            onDismissRequest = { showAssistant = false },
            sheetState = assistantSheetState,
            containerColor = Black,
            dragHandle = null,
            modifier = Modifier.fillMaxSize(),
        ) {
            AssistantContent(
                uiState = uiState,
                onInputChanged = assistantViewModel::onInputChanged,
                onSend = assistantViewModel::sendMessage,
            )
        }
    }
}

@Composable
private fun AiFloatingBubble(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = AccentPurple,
                spotColor = AccentPurple,
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(AccentPurple, AccentBlue.copy(alpha = 0.85f)),
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = "Open Campus Assistant",
            tint = Color.White,
            modifier = Modifier.size(22.dp),
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
