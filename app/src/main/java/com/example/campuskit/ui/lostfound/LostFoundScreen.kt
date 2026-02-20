package com.example.campuskit.ui.lostfound

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campuskit.data.lostfound.LostFoundItem
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

@Composable
fun LostFoundScreen(viewModel: LostFoundViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState()
    var showPostDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        containerColor = Black,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = AccentPurple,
                contentColor = Black,
                shape = SquircleShape,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Post Item")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Lost & Found",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Help return items to their owners",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(items, key = { it.id }) { item ->
                LostFoundCard(
                    item = item,
                    onWhatsApp = { number ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$number"))
                        context.startActivity(intent)
                    },
                    onTelegram = { username ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$username"))
                        context.startActivity(intent)
                    },
                    onPhone = { number ->
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                        context.startActivity(intent)
                    },
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showPostDialog) {
        PostItemDialog(
            onDismiss = { showPostDialog = false },
            onPost = { name, location, contact ->
                viewModel.addItem(
                    LostFoundItem(
                        id = System.currentTimeMillis().toString(),
                        itemName = name,
                        foundLocation = location,
                        imageUrls = emptyList(),
                        timestamp = "Just now",
                        contactWhatsApp = contact,
                    ),
                )
                showPostDialog = false
            },
        )
    }
}

@Composable
private fun LostFoundCard(
    item: LostFoundItem,
    onWhatsApp: (String) -> Unit,
    onTelegram: (String) -> Unit,
    onPhone: (String) -> Unit,
) {
    val itemColors = listOf(AccentBlue, AccentPurple, AccentTeal, StatusGreen)
    val itemColor = itemColors[item.id.hashCode().mod(itemColors.size)]

    Card(
        shape = SquircleShape,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Item icon placeholder 
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(SquircleShapeSmall)
                    .background(itemColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.itemName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.displayLarge,
                    color = itemColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )

            if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.foundLocation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AccessTime,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact methods
            Text(
                text = "CONTACT VIA",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item.contactWhatsApp?.let { number ->
                    ContactButton(
                        label = "WhatsApp",
                        color = Color(0xFF25D366),
                        onClick = { onWhatsApp(number) },
                    )
                }
                item.contactTelegram?.let { username ->
                    ContactButton(
                        label = "Telegram",
                        color = Color(0xFF0088CC),
                        onClick = { onTelegram(username) },
                    )
                }
                item.contactPhone?.let { number ->
                    ContactButton(
                        label = "Call",
                        color = AccentBlue,
                        onClick = { onPhone(number) },
                        icon = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    icon: Boolean = false,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), SquircleShapeSmall)
            .padding(horizontal = 4.dp),
    ) {
        if (icon) {
            Icon(
                Icons.Filled.Phone,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun PostItemDialog(
    onDismiss: () -> Unit,
    onPost: (name: String, location: String, contact: String) -> Unit,
) {
    var itemName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        shape = SquircleShape,
        title = {
            Text("Post Found Item", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Found Location") },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("WhatsApp Number") },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (itemName.isNotBlank() && location.isNotBlank()) {
                        onPost(itemName, location, contact)
                    }
                },
            ) {
                Text("Publish", color = AccentPurple, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextTertiary)
            }
        },
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentPurple,
    unfocusedBorderColor = SurfaceVariant,
    focusedLabelColor = AccentPurple,
    cursorColor = AccentPurple,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
)
