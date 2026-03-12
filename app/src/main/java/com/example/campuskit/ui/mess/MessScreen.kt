package com.example.campuskit.ui.mess

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AccentTeal
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.StatusGreen
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.SquircleShapeSmall
import com.example.campuskit.ui.theme.Surface
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TagSelectedBg
import com.example.campuskit.ui.theme.TagSelectedText
import com.example.campuskit.ui.theme.TagUnselectedBg
import com.example.campuskit.ui.theme.TagUnselectedText
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary
import com.example.campuskit.ui.theme.YuckRed
import com.example.campuskit.ui.theme.YuckRedContainer
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessScreen(viewModel: MessViewModel = hiltViewModel()) {
    val todayMenu by viewModel.todayMenu.collectAsState()
    val yuckItems by viewModel.yuckItems.collectAsState()
    var showDayPicker by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<DayOfWeek?>(null) }
    var showWeeklyView by remember { mutableStateOf(false) }
    val weeklyMenu by viewModel.weeklyMenu.collectAsState()

    val dayName = todayMenu.day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    Scaffold(containerColor = Black) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Mess Menu",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentTeal,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Yuck alert banner
            val yuckNames = yuckItems.map { it.itemName }
            val allMeals = todayMenu.breakfast + todayMenu.lunch + todayMenu.snacks + todayMenu.dinner
            val detectedYuck = allMeals.filter { meal ->
                yuckNames.any { yuck -> meal.contains(yuck, ignoreCase = true) }
            }
            if (detectedYuck.isNotEmpty()) {
                item {
                    Card(
                        shape = SquircleShapeSmall,
                        colors = CardDefaults.cardColors(containerColor = YuckRedContainer),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = YuckRed,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Yuck Alert!",
                                    color = YuckRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    text = detectedYuck.joinToString(", ") + " detected today",
                                    color = YuckRed.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }

            // Meal cards with stagger animation
            item {
                AnimatedMealCard(index = 0) {
                    MealCard(
                        title = "🌅  Breakfast",
                        items = todayMenu.breakfast,
                        yuckItems = yuckItems.map { it.itemName },
                        viewModel = viewModel,
                    )
                }
            }
            item {
                AnimatedMealCard(index = 1) {
                    MealCard(
                        title = "☀️  Lunch",
                        items = todayMenu.lunch,
                        yuckItems = yuckItems.map { it.itemName },
                        viewModel = viewModel,
                    )
                }
            }
            item {
                AnimatedMealCard(index = 2) {
                    MealCard(
                        title = "☕  Snacks",
                        items = todayMenu.snacks,
                        yuckItems = yuckItems.map { it.itemName },
                        viewModel = viewModel,
                    )
                }
            }
            item {
                AnimatedMealCard(index = 3) {
                    MealCard(
                        title = "🌙  Dinner",
                        items = todayMenu.dinner,
                        yuckItems = yuckItems.map { it.itemName },
                        viewModel = viewModel,
                    )
                }
            }

            // Yuck filter section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Your Yuck List",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = { showDayPicker = true }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AccentBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add yuck item",
                                tint = AccentBlue,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }

            item {
                if (yuckItems.isEmpty()) {
                    Text(
                        text = "No items yet. Add food items you dislike to get alerts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        yuckItems.forEach { item ->
                            InputChip(
                                selected = true,
                                onClick = { viewModel.removeYuckItem(item.itemName) },
                                label = { Text(item.itemName, fontSize = 13.sp) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                                shape = SquircleShapeSmall,
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = YuckRedContainer,
                                    selectedLabelColor = YuckRed,
                                    selectedTrailingIconColor = YuckRed,
                                ),
                            )
                        }
                    }
                }
            }

            // Weekly view toggle
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWeeklyView = !showWeeklyView }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Full Week Menu",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        if (showWeeklyView) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                    )
                }
            }

            if (showWeeklyView) {
                weeklyMenu.forEach { dayMenu ->
                    val name = dayMenu.day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    item(key = dayMenu.day) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    item {
                        MealCard(
                            title = "Breakfast",
                            items = dayMenu.breakfast,
                            yuckItems = yuckItems.map { it.itemName },
                            viewModel = viewModel,
                            compact = true,
                        )
                    }
                    item {
                        MealCard(
                            title = "Lunch",
                            items = dayMenu.lunch,
                            yuckItems = yuckItems.map { it.itemName },
                            viewModel = viewModel,
                            compact = true,
                        )
                    }
                    item {
                        MealCard(
                            title = "Snacks",
                            items = dayMenu.snacks,
                            yuckItems = yuckItems.map { it.itemName },
                            viewModel = viewModel,
                            compact = true,
                        )
                    }
                    item {
                        MealCard(
                            title = "Dinner",
                            items = dayMenu.dinner,
                            yuckItems = yuckItems.map { it.itemName },
                            viewModel = viewModel,
                            compact = true,
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Step 1: Day picker dialog
    if (showDayPicker) {
        DayPickerDialog(
            onDismiss = { showDayPicker = false },
            onDaySelected = { day ->
                selectedDay = day
                showDayPicker = false
            },
        )
    }

    // Step 2: Bottom sheet with category tabs + item list
    if (selectedDay != null) {
        AddYuckBottomSheet(
            day = selectedDay!!,
            viewModel = viewModel,
            yuckItems = yuckItems,
            onDismiss = { selectedDay = null },
        )
    }
}

@Composable
private fun AnimatedMealCard(index: Int, content: @Composable () -> Unit) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(index) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 350,
                delayMillis = index * 100,
            ),
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animProgress.value
                translationY = (1f - animProgress.value) * 30f
            },
    ) {
        content()
    }
}

@Composable
private fun MealCard(
    title: String,
    items: List<String>,
    yuckItems: List<String>,
    viewModel: MessViewModel,
    compact: Boolean = false,
) {
    Card(
        shape = SquircleShapeSmall,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 16.dp),
        ) {
            Text(
                text = title,
                style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))
            items.forEach { item ->
                val isYuck = yuckItems.any { yuck -> item.contains(yuck, ignoreCase = true) }
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isYuck) YuckRed else TextTertiary),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isYuck) YuckRed else TextSecondary,
                        fontWeight = if (isYuck) FontWeight.SemiBold else FontWeight.Normal,
                    )
                    if (isYuck) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YUCK",
                            fontSize = 9.sp,
                            color = YuckRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(YuckRedContainer, SquircleShapeSmall)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

// ── Step 1: Day Picker Dialog ──

@Composable
private fun DayPickerDialog(
    onDismiss: () -> Unit,
    onDaySelected: (DayOfWeek) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        shape = SquircleShape,
        title = {
            Text("Choose a Day", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Column {
                Text(
                    text = "Select a day to browse the mess menu and mark items you dislike.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DayOfWeek.entries.forEach { day ->
                        val label = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        Box(
                            modifier = Modifier
                                .clip(SquircleShapeSmall)
                                .background(SurfaceVariant)
                                .clickable { onDaySelected(day) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextTertiary)
            }
        },
    )
}

// ── Step 2: Bottom Sheet with Category Tabs + Item List ──

private enum class MealCategory(val label: String, val emoji: String) {
    BREAKFAST("Breakfast", "🌅"),
    LUNCH("Lunch", "☀️"),
    SNACKS("Snacks", "☕"),
    DINNER("Dinner", "🌙"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddYuckBottomSheet(
    day: DayOfWeek,
    viewModel: MessViewModel,
    yuckItems: List<com.example.campuskit.data.mess.YuckItemEntity>,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableIntStateOf(0) }
    val dayMenu = remember(day) { viewModel.getMenuForDay(day) }
    val yuckNames = yuckItems.map { it.itemName }

    val categories = MealCategory.entries
    val currentItems = when (categories[selectedCategory]) {
        MealCategory.BREAKFAST -> dayMenu.breakfast
        MealCategory.LUNCH -> dayMenu.lunch
        MealCategory.SNACKS -> dayMenu.snacks
        MealCategory.DINNER -> dayMenu.dinner
    }

    val dayDisplayName = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        shape = SquircleShape,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(TextTertiary),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            // Title
            Text(
                text = "Add Items to Yuck List",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dayDisplayName,
                style = MaterialTheme.typography.bodyMedium,
                color = AccentTeal,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category filter chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                categories.forEachIndexed { index, category ->
                    val selected = index == selectedCategory
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = index },
                        label = {
                            Text(
                                text = "${category.emoji} ${category.label}",
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) TextPrimary else TagUnselectedText,
                            )
                        },
                        shape = SquircleShapeSmall,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = TagUnselectedBg,
                            labelColor = TagUnselectedText,
                            selectedContainerColor = AccentBlue.copy(alpha = 0.2f),
                            selectedLabelColor = TextPrimary,
                        ),
                        border = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = SurfaceVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Item list
            if (currentItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No items for this meal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(currentItems) { item ->
                        val isAlreadyYuck = yuckNames.any { yuck ->
                            item.contains(yuck, ignoreCase = true)
                        }
                        YuckItemRow(
                            itemName = item,
                            isYuck = isAlreadyYuck,
                            onYuckClick = { viewModel.addYuckItem(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YuckItemRow(
    itemName: String,
    isYuck: Boolean,
    onYuckClick: () -> Unit,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isYuck) YuckRedContainer else CardBackground,
        animationSpec = tween(300),
        label = "yuckRowBg",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SquircleShapeSmall)
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = itemName,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isYuck) YuckRed else TextPrimary,
            fontWeight = if (isYuck) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        if (isYuck) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(StatusGreen.copy(alpha = 0.15f), SquircleShapeSmall)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = StatusGreen,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Added",
                    fontSize = 12.sp,
                    color = StatusGreen,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(SquircleShapeSmall)
                    .background(YuckRedContainer)
                    .clickable { onYuckClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Yuck!",
                    fontSize = 12.sp,
                    color = YuckRed,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
