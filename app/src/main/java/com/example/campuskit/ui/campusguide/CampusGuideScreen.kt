package com.example.campuskit.ui.campusguide

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
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Directions
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.data.campusguide.CampusGuideCatalog
import com.example.campuskit.data.campusguide.NearbyPlace
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
fun CampusGuideScreen() {
    val allPlaces = remember { CampusGuideCatalog.getAll() }
    val categories = remember { listOf("All") + CampusGuideCatalog.getCategories() }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredPlaces = remember(selectedCategory) {
        if (selectedCategory == "All") allPlaces
        else allPlaces.filter { it.category == selectedCategory }
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
                    text = "Campus Guide",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Explore nearby spots around campus",
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
                                selectedContainerColor = AccentTeal.copy(alpha = 0.2f),
                                selectedLabelColor = AccentTeal,
                                containerColor = SurfaceVariant.copy(alpha = 0.5f),
                                labelColor = TextSecondary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = SurfaceVariant,
                                selectedBorderColor = AccentTeal.copy(alpha = 0.4f),
                                enabled = true,
                                selected = category == selectedCategory,
                            ),
                            shape = SquircleShapeSmall,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(filteredPlaces, key = { index, place -> "${place.name}_$index" }) { index, place ->
                AnimatedPlaceCard(index = index) {
                    PlaceCard(
                        place = place,
                        onDirections = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(place.mapLink))
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
private fun AnimatedPlaceCard(index: Int, content: @Composable () -> Unit) {
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
private fun PlaceCard(
    place: NearbyPlace,
    onDirections: () -> Unit,
) {
    val categoryColor = when (place.category) {
        "Restaurant" -> StatusGreen
        "Hotel" -> AccentPurple
        "Shop" -> AccentBlue
        "Landmark" -> AccentTeal
        else -> AccentBlue
    }

    val categoryIcon: ImageVector = when (place.category) {
        "Restaurant" -> Icons.Filled.Restaurant
        "Hotel" -> Icons.Filled.Hotel
        "Shop" -> Icons.Filled.ShoppingBag
        "Landmark" -> Icons.Filled.Museum
        else -> Icons.Filled.LocationOn
    }

    Card(
        shape = SquircleShape,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDirections),
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.distance,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Directions button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.12f))
                    .clickable(onClick = onDirections),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Directions,
                    contentDescription = "Open in Maps",
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
