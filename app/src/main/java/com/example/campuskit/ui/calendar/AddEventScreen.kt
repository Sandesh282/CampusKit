package com.example.campuskit.ui.calendar

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.ui.theme.CampusKitTheme
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.StatusRed
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary
import com.example.campuskit.data.calendar.CalendarEvent
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onDismiss: () -> Unit,
    onSave: (CalendarEvent) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isAllDay by remember { mutableStateOf(false) }

    var participant by remember { mutableStateOf("") }
    var conferencing by remember { mutableStateOf("") }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val startTimePickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0)
    val endTimePickerState = rememberTimePickerState(initialHour = 10, initialMinute = 0)
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    
    var timeZoneExpanded by remember { mutableStateOf(false) }
    var timeZone by remember { mutableStateOf("CST") }
    var repeatExpanded by remember { mutableStateOf(false) }
    var repeat by remember { mutableStateOf("Never") }
    
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()) }
    val dateText = datePickerState.selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: "Select Date"
    val formatTime = { state: androidx.compose.material3.TimePickerState -> String.format(Locale.getDefault(), "%02d:%02d", state.hour, state.minute) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = StatusRed, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            
            Text(
                "New Event", 
                color = TextPrimary, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )
            
            TextButton(onClick = {
                val date = datePickerState.selectedDateMillis?.let {
                    java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()

                val start = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)
                val end = LocalTime.of(endTimePickerState.hour, endTimePickerState.minute)
                
                // Keep the deep AMOED vibe by choosing a random theme color, defaulting to CalendarPink
                val randomColor = listOf(
                    com.example.campuskit.ui.theme.CalendarPink,
                    com.example.campuskit.ui.theme.CalendarPeach,
                    com.example.campuskit.ui.theme.CalendarSkyBlue,
                    com.example.campuskit.ui.theme.CalendarGreen,
                    com.example.campuskit.ui.theme.CalendarLavender
                ).random()

                val newEvent = CalendarEvent(
                    id = UUID.randomUUID().toString(),
                    title = title.ifEmpty { "New Event" },
                    date = date,
                    startTime = start,
                    endTime = end,
                    color = randomColor,
                    assignee = participant,
                    attendeeCount = if (participant.isNotEmpty()) 1 else 0,
                    location = location,
                    description = description
                )
                
                onSave(newEvent)
                onDismiss()
            }) {
                Text("Save", color = AccentBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title Field
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { 
                    Text("Title", color = TextSecondary, fontSize = 20.sp, fontWeight = FontWeight.Medium) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SquircleShape),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Timing Details Block
            BlockCard {
                // Times
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerButton(
                        time = formatTime(startTimePickerState), 
                        icon = Icons.Filled.AccessTime,
                        onClick = { showStartTimePicker = true }
                    )
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                    TimePickerButton(
                        time = formatTime(endTimePickerState), 
                        icon = Icons.Filled.AccessTime,
                        onClick = { showEndTimePicker = true }
                    )
                }
                
                BlockDivider()

                // Date
                IconListItem(
                    icon = Icons.Filled.CalendarMonth,
                    text = dateText,
                    onClick = { showDatePicker = true }
                )
                
                BlockDivider()

                // All Day
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("All day", color = TextPrimary, fontSize = 16.sp)
                    }
                    Switch(
                        checked = isAllDay,
                        onCheckedChange = { isAllDay = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Black,
                            checkedTrackColor = AccentBlue,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceVariant
                        )
                    )
                }

                BlockDivider()

                // Timezone
                Box {
                    IconListItem(
                        icon = Icons.Filled.Public,
                        text = "Time zone",
                        trailingText = timeZone,
                        onClick = { timeZoneExpanded = true }
                    )
                    DropdownMenu(
                        expanded = timeZoneExpanded,
                        onDismissRequest = { timeZoneExpanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        listOf("CST", "EST", "PST", "UTC").forEach { tz ->
                            DropdownMenuItem(
                                text = { Text(tz, color = TextPrimary) },
                                onClick = { timeZone = tz; timeZoneExpanded = false }
                            )
                        }
                    }
                }

                BlockDivider()

                // Repeat
                Box {
                    IconListItem(
                        icon = Icons.Filled.Repeat,
                        text = "Repeat",
                        trailingText = repeat,
                        onClick = { repeatExpanded = true }
                    )
                    DropdownMenu(
                        expanded = repeatExpanded,
                        onDismissRequest = { repeatExpanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        listOf("Never", "Every day", "Every week", "Every month").forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r, color = TextPrimary) },
                                onClick = { repeat = r; repeatExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details Block
            BlockCard {
                IconTextFieldListItem(
                    icon = Icons.Filled.Person,
                    value = participant,
                    onValueChange = { participant = it },
                    placeholder = "Participant"
                )
                BlockDivider()
                IconTextFieldListItem(
                    icon = Icons.Filled.Videocam,
                    value = conferencing,
                    onValueChange = { conferencing = it },
                    placeholder = "Conferencing"
                )
                BlockDivider()
                IconTextFieldListItem(
                    icon = Icons.Filled.LocationOn,
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Location"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description Block
            BlockCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Description", color = TextSecondary, fontSize = 16.sp)
                        }

                        // Mock Use AI button
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AccentBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "✨ Use AI",
                                color = AccentBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Add any extra notes or links here...", color = TextTertiary, fontSize = 15.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        if (showStartTimePicker) {
            TimePickerDialog(
                title = "Select Start Time",
                onCancel = { showStartTimePicker = false },
                onConfirm = { showStartTimePicker = false }
            ) {
                TimePicker(state = startTimePickerState)
            }
        }
        
        if (showEndTimePicker) {
            TimePickerDialog(
                title = "Select End Time",
                onCancel = { showEndTimePicker = false },
                onConfirm = { showEndTimePicker = false }
            ) {
                TimePicker(state = endTimePickerState)
            }
        }
        
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("OK", color = AccentBlue) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = TextPrimary) }
                },
                colors = DatePickerDefaults.colors(containerColor = CardBackground)
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun BlockCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SquircleShape)
            .background(CardBackground)
    ) {
        content()
    }
}

@Composable
private fun BlockDivider() {
    HorizontalDivider(
        color = SurfaceVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(start = 54.dp, end = 16.dp)
    )
}

@Composable
private fun TimePickerButton(time: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .clip(SquircleShape)
            .background(SurfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(time, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun IconListItem(
    icon: ImageVector,
    text: String,
    textColor: Color = TextPrimary,
    trailingText: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, color = textColor, fontSize = 16.sp)
        }
        
        if (trailingText != null) {
            Text(trailingText, color = TextSecondary, fontSize = 15.sp)
        }
    }
}

@Composable
private fun IconTextFieldListItem(
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary, fontSize = 16.sp) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onCancel,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = CardBackground
                ),
            color = CardBackground
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
                content()
                Row(modifier = Modifier.height(40.dp).fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onCancel) { Text("Cancel", color = TextPrimary) }
                    TextButton(onClick = onConfirm) { Text("OK", color = AccentBlue) }
                }
            }
        }
    }
}
@Preview(
    name = "Add Event Screen",
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun AddEventScreenPreview() {
    CampusKitTheme {
        AddEventScreen(
            onDismiss = {},
            onSave = {}
        )
    }
}