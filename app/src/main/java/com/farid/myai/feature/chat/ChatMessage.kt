package com.farid.myai.feature.chat

import android.graphics.Bitmap
import java.util.UUID

enum class Participant {
    USER, MODEL, ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var selectedImages: List<Bitmap> = emptyList(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
)
