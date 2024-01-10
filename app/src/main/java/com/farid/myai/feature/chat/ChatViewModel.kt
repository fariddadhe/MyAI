package com.farid.myai.feature.chat

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farid.myai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val geminiPro by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        ).apply {
            startChat()
        }
    }

    private val geminiProVision by lazy {
        GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = BuildConfig.apiKey
        ).apply {
            startChat()
        }
    }

    private val chatv2 = mutableListOf<Triple<String, String, List<Bitmap>?>>()

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Hello, I'm Farid") },
            content(role = "model") { text("Great to meet you. What would you like to know?") }
        )
    )

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState(chat.history.map { content ->
            ChatMessage(
                text = content.parts.first().asTextOrNull() ?: "",
                participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                isPending = false
            )
        }))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userMessage: String, selectedImages: List<Bitmap>?){


        _uiState.value.addMesages(
            ChatMessage(
                text = userMessage,
                selectedImages = selectedImages ?: emptyList(),
                participant = Participant.USER,
                isPending = true
            )
        )
        val prompt = "Look at the image(s), and then answer the following question: $userMessage"

        val generativeModelV2 = if(selectedImages != null) geminiProVision else geminiPro

        viewModelScope.launch {
            try {
                if(selectedImages != null) {
                    val inputContent = content {
                        for (bitmap in selectedImages){
                            image(bitmap)
                        }
                        text(prompt)
                    }

                    val respone = generativeModelV2.generateContent(inputContent)

                    _uiState.value.replaceLastPendingMessaging()

                    respone.text?.let { modelResponse ->
                        _uiState.value.addMesages(
                            ChatMessage(
                                text = modelResponse,
                                participant = Participant.MODEL,
                                isPending = false
                            )
                        )
                    }
                }else{
                    val respone = generativeModelV2.generateContent(userMessage)

                    _uiState.value.replaceLastPendingMessaging()

                    respone.text?.let { modelResponse ->
                        _uiState.value.addMesages(
                            ChatMessage(
                                text = modelResponse,
                                participant = Participant.MODEL,
                                isPending = false
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessaging()
                _uiState.value.addMesages(
                    ChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }

}