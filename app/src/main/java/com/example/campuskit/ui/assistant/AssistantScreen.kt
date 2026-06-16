package com.example.campuskit.ui.assistant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AccentPurple
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.CampusKitTheme
import com.example.campuskit.ui.theme.Surface
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary

// ── Stateful entry point (requires Hilt) ──────────────────────────────────────

@Composable
fun AssistantScreen(
    viewModel: AssistantViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AssistantContent(
        uiState = uiState,
        onInputChanged = viewModel::onInputChanged,
        onSend = viewModel::sendMessage,
    )
}

// ── Stateless content (previewable) ───────────────────────────────────────────

@Composable
fun AssistantContent(
    uiState: AssistantUiState,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
) {
    val listState = rememberLazyListState()

    // Auto-scroll to the latest message
    LaunchedEffect(uiState.messages.size, uiState.messages.lastOrNull()?.text) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .imePadding(),
    ) {
        // Compact inline title (sits below the sheet's drag handle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "AI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentPurple)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Campus Assistant",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = "Powered by Gemini",
                    fontSize = 10.sp,
                    color = TextTertiary,
                )
            }
        }

        if (uiState.messages.isEmpty()) {
            WelcomePrompt(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(message = message)
                }
                item {
                    AnimatedVisibility(
                        visible = uiState.isLoading && uiState.messages.lastOrNull()?.text?.isEmpty() == true,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        TypingIndicator()
                    }
                }
            }
        }

        ChatInputBar(
            value = uiState.inputText,
            onValueChange = onInputChanged,
            onSend = onSend,
            isLoading = uiState.isLoading,
        )
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────


@Composable
private fun WelcomePrompt(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AccentPurple.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "AI", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AccentPurple)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Ask me anything about campus",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try: \"Am I safe to bunk COA?\" or \"What's for dinner today?\"",
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp,
                    )
                )
                .background(if (isUser) AccentBlue.copy(alpha = 0.85f) else CardBackground)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = message.text.ifBlank { " " },
                fontSize = 14.sp,
                color = if (isUser) Color.White else TextPrimary,
                lineHeight = 21.sp,
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot$index",
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(TextSecondary.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = {
                Text("Ask about campus...", color = TextTertiary, fontSize = 14.sp)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            maxLines = 4,
            singleLine = false,
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank() && !isLoading,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (value.isNotBlank() && !isLoading) AccentBlue else SurfaceVariant),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (value.isNotBlank() && !isLoading) Color.White else TextTertiary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Welcome state")
@Composable
private fun PreviewAssistantEmpty() {
    CampusKitTheme {
        AssistantContent(
            uiState = AssistantUiState(),
            onInputChanged = {},
            onSend = {},
        )
    }
}

@Preview(showBackground = true, name = "Conversation in progress")
@Composable
private fun PreviewAssistantConversation() {
    CampusKitTheme {
        AssistantContent(
            uiState = AssistantUiState(
                messages = listOf(
                    ChatMessage("Am I safe to bunk COA tomorrow?", isUser = true),
                    ChatMessage("Yes! You've attended 20 out of 30 COA classes (66%). Your minimum is 75%, which means you need 2 more bunks before you drop below. You have 2 safe bunks remaining.", isUser = false),
                    ChatMessage("What's for dinner today?", isUser = true),
                    ChatMessage("Tonight's dinner is Arahar Dal Fry, Aaloo Patta Gobhi, Roti, Rice, and Salad. Enjoy!", isUser = false),
                ),
                inputText = "Are there any events this week?",
            ),
            onInputChanged = {},
            onSend = {},
        )
    }
}

@Preview(showBackground = true, name = "Typing indicator")
@Composable
private fun PreviewAssistantLoading() {
    CampusKitTheme {
        AssistantContent(
            uiState = AssistantUiState(
                messages = listOf(
                    ChatMessage("What events are happening this weekend?", isUser = true),
                    ChatMessage("", isUser = false), // empty = typing indicator shows
                ),
                isLoading = true,
            ),
            onInputChanged = {},
            onSend = {},
        )
    }
}
