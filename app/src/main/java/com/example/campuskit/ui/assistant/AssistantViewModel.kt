package com.example.campuskit.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.domain.assistant.AskCampusAssistantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Represents a single message in the chat. */
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
)

data class AssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val inputText: String = "",
    val error: String? = null,
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val askCampusAssistant: AskCampusAssistantUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun onInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, error = null)
    }

    fun sendMessage() {
        val query = _uiState.value.inputText.trim()
        if (query.isBlank()) return

        // Append user message and clear input
        val userMessage = ChatMessage(text = query, isUser = true)
        val placeholderAssistant = ChatMessage(text = "", isUser = false)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage + placeholderAssistant,
            inputText = "",
            isLoading = true,
            error = null,
        )

        viewModelScope.launch {
            askCampusAssistant(query)
                .catch { e ->
                    // Replace the placeholder with an error message
                    updateLastAssistantMessage("Sorry, something went wrong. Please try again.")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect { token ->
                    // Append each streamed token to the last assistant message
                    updateLastAssistantMessage(
                        _uiState.value.messages.last().text + token
                    )
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    /** Replaces the text of the last message in the list (always the assistant's). */
    private fun updateLastAssistantMessage(newText: String) {
        val messages = _uiState.value.messages.toMutableList()
        if (messages.isNotEmpty()) {
            messages[messages.lastIndex] = messages.last().copy(text = newText)
        }
        _uiState.value = _uiState.value.copy(messages = messages)
    }
}
