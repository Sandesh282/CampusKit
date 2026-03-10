package com.example.campuskit.ui.quickreads

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.data.quickreads.QuickRead
import com.example.campuskit.data.quickreads.QuickReadsCatalog
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AccentPurple
import com.example.campuskit.ui.theme.AccentTeal
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.SquircleShapeSmall
import com.example.campuskit.ui.theme.StatusGreen
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickReadsScreen() {
    val allReads = remember { QuickReadsCatalog.getAll() }
    val categories = remember { listOf("All") + QuickReadsCatalog.getCategories() }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredReads = remember(selectedCategory) {
        if (selectedCategory == "All") allReads
        else allReads.filter { it.category == selectedCategory }
    }

    val context = LocalContext.current

    Scaffold(containerColor = Black) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quick Reads",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Curated articles to level up your skills",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Category chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    fontWeight = if (category == selectedCategory) FontWeight.SemiBold else FontWeight.Normal,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple.copy(alpha = 0.2f),
                                selectedLabelColor = AccentPurple,
                                containerColor = SurfaceVariant.copy(alpha = 0.5f),
                                labelColor = TextSecondary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = SurfaceVariant,
                                selectedBorderColor = AccentPurple.copy(alpha = 0.4f),
                                enabled = true,
                                selected = category == selectedCategory,
                            ),
                            shape = SquircleShapeSmall,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(filteredReads, key = { _, read -> read.id }) { index, read ->
                AnimatedReadCard(index = index) {
                    QuickReadCard(
                        read = read,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(read.url))
                            context.startActivity(intent)
                        },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun AnimatedReadCard(index: Int, content: @Composable () -> Unit) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(index) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, delayMillis = index * 60),
        )
    }
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = animProgress.value
            translationY = (1f - animProgress.value) * 30f
        },
    ) {
        content()
    }
}

@Composable
private fun QuickReadCard(
    read: QuickRead,
    onClick: () -> Unit,
) {
    val categoryColor = when (read.category) {
        "Coding" -> AccentBlue
        "AI & ML" -> AccentPurple
        "College Life" -> StatusGreen
        "Open Source" -> AccentTeal
        else -> AccentBlue
    }

    val categoryIcon: ImageVector = when (read.category) {
        "Coding" -> Icons.Filled.Code
        "AI & ML" -> Icons.Filled.Psychology
        "College Life" -> Icons.Filled.School
        "Open Source" -> Icons.AutoMirrored.Filled.MenuBook
        else -> Icons.Filled.Code
    }

    Card(
        shape = SquircleShape,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Category icon badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = read.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = read.source,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open link",
                tint = TextTertiary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
