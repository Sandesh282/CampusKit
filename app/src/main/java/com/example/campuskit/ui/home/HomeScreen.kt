package com.example.campuskit.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Cancel
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuskit.data.attendance.AttendanceEntity
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AttendanceCritical
import com.example.campuskit.ui.theme.AttendanceSafe
import com.example.campuskit.ui.theme.AttendanceWarning
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary

@Composable
fun HomeScreen(viewModel: AttendanceViewModel = viewModel()) {
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Black,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentBlue,
                contentColor = Black,
                shape = SquircleShape,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Subject")
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
                    text = "Attendance",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your safe-to-bunk margin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (subjects.isEmpty()) {
                item {
                    EmptyState()
                }
            }

            items(subjects, key = { it.subjectId }) { subject ->
                AttendanceCard(
                    subject = subject,
                    onAttend = { viewModel.markAttended(subject.subjectId) },
                    onBunk = { viewModel.markBunked(subject.subjectId) },
                    onDelete = { viewModel.deleteSubject(subject) },
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name ->
                viewModel.addSubject(name)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "📚",
            fontSize = 48.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No subjects yet",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to add your first subject\nand start tracking attendance",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AttendanceCard(
    subject: AttendanceEntity,
    onAttend: () -> Unit,
    onBunk: () -> Unit,
    onDelete: () -> Unit,
) {
    val percentage = AttendanceViewModel.calculatePercentage(subject.attendedClasses, subject.totalClasses)
    val status = AttendanceViewModel.getStatus(subject.attendedClasses, subject.totalClasses, subject.minimumPercentage)

    val statusColor by animateColorAsState(
        targetValue = when (status) {
            is AttendanceStatus.Safe -> AttendanceSafe
            is AttendanceStatus.Warning -> AttendanceWarning
            is AttendanceStatus.Critical -> AttendanceCritical
            is AttendanceStatus.NoData -> TextTertiary
        },
        animationSpec = tween(500),
        label = "statusColor",
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SquircleShape,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularAttendanceProgress(
                percentage = percentage,
                color = statusColor,
                modifier = Modifier.size(80.dp),
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.subjectName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${subject.attendedClasses} / ${subject.totalClasses} classes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))

                val statusText = when (status) {
                    is AttendanceStatus.Safe -> "Safe to bunk: ${status.canBunk} classes"
                    is AttendanceStatus.Warning -> "Caution: ${status.canBunk} bunks left"
                    is AttendanceStatus.Critical -> "Critical: attend next ${status.needToAttend}"
                    is AttendanceStatus.NoData -> "No data yet"
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TextButton(onClick = onAttend) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = AttendanceSafe,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Attended", color = AttendanceSafe, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            TextButton(onClick = onBunk) {
                Icon(
                    Icons.Outlined.Cancel,
                    contentDescription = null,
                    tint = AttendanceCritical,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bunked", color = AttendanceCritical, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
fun CircularAttendanceProgress(
    percentage: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(800),
        label = "progress",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (animatedPercentage / 100f) * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        Text(
            text = "${animatedPercentage.toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
) {
    var subjectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        shape = SquircleShape,
        title = {
            Text("Add Subject", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Subject Name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = AccentBlue,
                    cursorColor = AccentBlue,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (subjectName.isNotBlank()) onAdd(subjectName) },
            ) {
                Text("Add", color = AccentBlue, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextTertiary)
            }
        },
    )
}
