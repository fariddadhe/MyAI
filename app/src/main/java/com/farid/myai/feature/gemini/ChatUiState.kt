package com.farid.myai.feature.gemini

import androidx.compose.runtime.toMutableStateList

class ChatUiState (
    message: List<ChatMessage> = emptyList()
){
    private val _messages: MutableList<ChatMessage> = message.toMutableStateList()
    val message: List<ChatMessage> = _messages

    fun addMesages(msg: ChatMessage){
        _messages.add(msg)
    }

    fun replaceLastPendingMessaging() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }
}