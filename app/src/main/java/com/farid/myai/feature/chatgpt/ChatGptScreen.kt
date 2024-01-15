package com.farid.myai.feature.chatgpt

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.request.SuccessResult
import coil.size.Precision
import com.farid.myai.feature.gemini.ChatMessage
import com.farid.myai.feature.gemini.Participant
import com.farid.myai.models.Message
import com.farid.myai.ui.theme.DarkGreen
import com.farid.myai.ui.theme.GreyBlue
import com.farid.myai.ui.theme.LightBlue
import com.farid.myai.ui.theme.PrimaryColor
import com.farid.myai.ui.theme.WhiteTextColor
import kotlinx.coroutines.launch

@Composable
fun ChatGptScreen(
    navController: NavController,
    viewModel: ChatGptViewModle = viewModel()
){
    val message by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val loading by viewModel.loading.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = PrimaryColor,
        bottomBar = {
            MessageInput(
                onReasonClicked = { inputText ->
                    coroutineScope.launch {
                        viewModel.askQuestion(inputText)
                    }
                },
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                },
                loading
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ChatList(message, listState)
        }
    }
}

@Composable
private fun ChatList(
    chatMessage: List<Message>,
    listState: LazyListState
){
    LazyColumn(
        reverseLayout = true,
        state = listState,
        modifier = Modifier.padding(horizontal = 8.dp)
    ){
        items(chatMessage.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}

@Composable
private fun ChatBubbleItem(
    chatMessage: Message
){
    val isModelMessage = chatMessage.role == "assistant" || chatMessage.role == "error"

    val backgroundColor = when (chatMessage.role) {
        "assistant" -> DarkGreen
        "user" -> LightBlue
        else -> MaterialTheme.colorScheme.errorContainer
    }

    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(40.dp, 50.dp, 50.dp, 0.dp)
    } else {
        RoundedCornerShape(50.dp, 40.dp, 0.dp, 50.dp)
    }

    val horizontalAlignment = if (isModelMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row {
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    Text(
                        style = TextStyle(color = WhiteTextColor),
                        text = chatMessage.content,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageInput(
    onReasonClicked: (String) -> Unit = { _ -> },
    resetScroll: () -> Unit = {},
    loading: Boolean
){
    var userMessage by rememberSaveable { mutableStateOf("") }

    Column(Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
        TextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = DarkGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                if(loading){
                    CircularProgressIndicator()
                }else{
                    IconButton(
                        onClick = {
                            if (userMessage.isNotBlank()) {
                                onReasonClicked(userMessage)
                                userMessage = ""
                                resetScroll()
                            }
                        },
                    ) {
                        Icon(
                            Icons.Outlined.Send,
                            contentDescription = "",
                            tint = if(userMessage.isNotBlank()) Color.White else GreyBlue,
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(30.dp))
        )
    }
}






