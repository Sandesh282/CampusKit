package com.example.campuskit.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campuskit.data.attendance.AttendanceEntity
import com.example.campuskit.domain.academic.model.Program
import com.example.campuskit.domain.academic.model.Subject
import com.example.campuskit.domain.attendance.AttendanceStatus
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AttendanceCritical
import com.example.campuskit.ui.theme.AttendanceSafe
import com.example.campuskit.ui.theme.AttendanceWarning
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.CardLavender
import com.example.campuskit.ui.theme.CardMint
import com.example.campuskit.ui.theme.CardPeach
import com.example.campuskit.ui.theme.CardRose
import com.example.campuskit.ui.theme.CardSky
import com.example.campuskit.ui.theme.CardTextDark
import com.example.campuskit.ui.theme.OverallAttendanceBg
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TagSelectedBg
import com.example.campuskit.ui.theme.TagSelectedText
import com.example.campuskit.ui.theme.TagUnselectedBg
import com.example.campuskit.ui.theme.TagUnselectedText
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary

private val pastelColors = listOf(CardMint, CardLavender, CardPeach, CardSky, CardRose)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToQuestionPapers: (String) -> Unit = {},
    onNavigateToNotes: (String) -> Unit = {},
) {
    val selectedTag by viewModel.selectedTag.collectAsState()
    val attendanceSubjects by viewModel.attendanceSubjects.collectAsState()
    val academicPrefs by viewModel.academicPreferences.collectAsState()
    val academicSubjects by viewModel.academicSubjects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showSemesterDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    val needsSetup = academicPrefs.program == Program.UNKNOWN

    Scaffold(
        containerColor = Black,
        floatingActionButton = {
            if (selectedTag == HomeTag.ATTENDANCE) {
                FloatingActionButton(
                    onClick = { showSemesterDialog = true },
                    containerColor = AccentBlue,
                    contentColor = Black,
                    shape = SquircleShape,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Subject")
                }
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
            // ── Header ──
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContent(
                    targetState = isSearchActive,
                    label = "search_header_anim"
                ) { searchActive ->
                    if (searchActive) {
                        HomeSearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            onClose = {
                                isSearchActive = false
                                viewModel.updateSearchQuery("")
                            }
                        )
                    } else {
                        HomeHeader(
                            semester = academicPrefs.semester,
                            program = academicPrefs.program,
                            studentName = academicPrefs.studentName,
                            needsSetup = needsSetup,
                            onSearchTap = { isSearchActive = true },
                            onSemesterTap = { showSemesterDialog = true },
                        )
                    }
                }
            }

            // ── Tag Row ──
            item {
                TagRow(
                    selectedTag = selectedTag,
                    onTagSelected = { viewModel.selectTag(it) },
                )
            }

            // ── Setup Prompt ──
            if (needsSetup && selectedTag != HomeTag.ATTENDANCE) {
                item {
                    SetupPromptCard(onSetup = { showSemesterDialog = true })
                }
            }

            // ── Content ──
            when (selectedTag) {
                HomeTag.ATTENDANCE -> {
                    // Overall attendance summary
                    if (attendanceSubjects.isNotEmpty()) {
                        item {
                            OverallAttendanceSummary(
                                subjects = attendanceSubjects,
                                onSeeAll = { /* already on attendance tab */ },
                            )
                        }
                    }

                    if (attendanceSubjects.isEmpty()) {
                        item { EmptyState() }
                    }

                    items(attendanceSubjects, key = { it.subjectId }) { subject ->
                        AttendanceCard(
                            subject = subject,
                            viewModel = viewModel,
                            onAttend = { viewModel.markAttended(subject.subjectId) },
                            onBunk = { viewModel.markBunked(subject.subjectId) },
                            onDelete = { viewModel.deleteSubject(subject) },
                        )
                    }
                }

                HomeTag.QUESTION_PAPERS -> {
                    if (academicSubjects.isEmpty() && !needsSetup) {
                        item {
                            EmptyAcademicState("No subjects found for this semester.\nSync may be in progress.")
                        }
                    }
                    itemsIndexed(academicSubjects) { index, subject ->
                        AcademicSubjectCard(
                            subject = subject,
                            color = pastelColors[index % pastelColors.size],
                            label = "Question Papers",
                            onClick = { onNavigateToQuestionPapers(subject.code) },
                        )
                    }
                }

                HomeTag.NOTES -> {
                    if (academicSubjects.isEmpty() && !needsSetup) {
                        item {
                            EmptyAcademicState("No subjects found for this semester.\nSync may be in progress.")
                        }
                    }
                    itemsIndexed(academicSubjects) { index, subject ->
                        AcademicSubjectCard(
                            subject = subject,
                            color = pastelColors[(index + 2) % pastelColors.size],
                            label = "Notes",
                            onClick = { onNavigateToNotes(subject.code) },
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showSemesterDialog) {
        SemesterSelectionDialog(
            currentProgram = academicPrefs.program,
            currentSemester = academicPrefs.semester,
            onConfirm = { program, semester ->
                viewModel.updateProgram(program)
                viewModel.updateSemester(semester)
                viewModel.seedSubjectsForSelection(program, semester)
                viewModel.syncResources()
                showSemesterDialog = false
            },
            onDismiss = { showSemesterDialog = false },
        )
    }
}

// ── Header ──
@Composable
private fun HomeHeader(
    semester: Int,
    program: Program,
    studentName: String,
    needsSetup: Boolean,
    onSearchTap: () -> Unit,
    onSemesterTap: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (studentName.isNotBlank()) studentName else "Your Campus",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
            )
            if (!needsSetup) {
                Text(
                    text = "Semester $semester · ${program.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentBlue,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSemesterTap,
                        )
                        .padding(vertical = 2.dp),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSearchTap,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSemesterTap,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.MoreHoriz,
                    contentDescription = "More",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

// ── Search Bar ──
@Composable
private fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onClose) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Search", tint = TextPrimary)
        }
        
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search subjects...", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = SurfaceVariant,
                focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
            ),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            }
        )
    }
}

// ── Tag Row ──
@Composable
private fun TagRow(
    selectedTag: HomeTag,
    onTagSelected: (HomeTag) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val tags = listOf(
            HomeTag.ATTENDANCE to "Attendance",
            HomeTag.QUESTION_PAPERS to "Question Papers",
            HomeTag.NOTES to "Notes",
        )
        items(tags) { (tag, label) ->
            TagChip(
                label = label,
                isSelected = selectedTag == tag,
                onClick = { onTagSelected(tag) },
            )
        }
    }
}

@Composable
private fun TagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TagSelectedBg else TagUnselectedBg,
        animationSpec = tween(200),
        label = "tagBg",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) TagSelectedText else TagUnselectedText,
        animationSpec = tween(200),
        label = "tagText",
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

// ── Overall Attendance Summary ──
@Composable
private fun OverallAttendanceSummary(
    subjects: List<AttendanceEntity>,
    onSeeAll: () -> Unit,
) {
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val totalClasses = subjects.sumOf { it.totalClasses }
    val overallPercentage = if (totalClasses == 0) 100f
    else (totalAttended.toFloat() / totalClasses.toFloat()) * 100f

    val statusColor = when {
        overallPercentage >= 75f -> AttendanceSafe
        overallPercentage >= 60f -> AttendanceWarning
        else -> AttendanceCritical
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OverallAttendanceBg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Overall Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$totalAttended / $totalClasses classes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSeeAll,
                    ),
                )
            }

            CircularAttendanceProgress(
                percentage = overallPercentage,
                color = statusColor,
                modifier = Modifier.size(72.dp),
            )
        }
    }
}

// ── Setup Prompt ──
@Composable
private fun SetupPromptCard(onSetup: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSetup,
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AccentBlue.copy(alpha = 0.1f)),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "🎓 Set up your semester",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select your branch & semester to unlock subjects, question papers, and notes.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tap to set up →",
                style = MaterialTheme.typography.labelLarge,
                color = AccentBlue,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ── Academic Subject Card (Pastel Curved) ──
@Composable
private fun AcademicSubjectCard(
    subject: Subject,
    color: Color,
    label: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CardTextDark.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subject.name,
                style = MaterialTheme.typography.headlineMedium,
                color = CardTextDark,
                fontWeight = FontWeight.Bold,
            )
            if (subject.code.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subject.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = CardTextDark.copy(alpha = 0.5f),
                )
            }
        }
    }
}

// ── Empty States ──
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "📚", fontSize = 48.sp)
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
private fun EmptyAcademicState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "📄", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Attendance Card (preserved from existing) ──
@Composable
fun AttendanceCard(
    subject: AttendanceEntity,
    viewModel: HomeViewModel,
    onAttend: () -> Unit,
    onBunk: () -> Unit,
    onDelete: () -> Unit,
) {
    val status = viewModel.getAttendanceStatus(
        subject.attendedClasses, subject.totalClasses, subject.minimumPercentage,
    )
    val percentage = if (subject.totalClasses == 0) 100f
    else (subject.attendedClasses.toFloat() / subject.totalClasses.toFloat()) * 100f

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

// ── Circular Progress ──
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

