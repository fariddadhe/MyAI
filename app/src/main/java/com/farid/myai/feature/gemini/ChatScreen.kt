package com.farid.myai.feature.gemini

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.outlined.Clear
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import com.farid.myai.components.DialogShowImage
import com.farid.myai.ui.theme.DarkGreen
import com.farid.myai.ui.theme.GreyBlue
import com.farid.myai.ui.theme.LightBlue
import com.farid.myai.ui.theme.PrimaryColor
import com.farid.myai.ui.theme.WhiteTextColor
import com.farid.myai.util.UriSaver
import kotlinx.coroutines.launch


@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel()
){
    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val imageRequestBuilder = ImageRequest.Builder(LocalContext.current)
    val imageLoader = ImageLoader.Builder(LocalContext.current).build()

    Scaffold(
        containerColor = PrimaryColor,
        bottomBar = {
            MessageInput(
                onReasonClicked = { inputText, selectedItems ->
                    coroutineScope.launch {
                        if(selectedItems != null){
                            val bitmaps = selectedItems.mapNotNull {
                                val imageRequest = imageRequestBuilder
                                    .data(it)
                                    .size(size = 768)
                                    .precision(Precision.EXACT)
                                    .build()

                                try {
                                    val result = imageLoader.execute(imageRequest)
                                    if(result is SuccessResult){
                                        return@mapNotNull (result.drawable as BitmapDrawable).bitmap
                                    }else{
                                        return@mapNotNull null
                                    }
                                }catch (e: Exception){
                                    return@mapNotNull null
                                }
                            }
                            chatViewModel.sendMessage(inputText, bitmaps)
                        }else{
                            chatViewModel.sendMessage(inputText, null)
                        }
                    }
                },
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ChatList(chatUiState.message, listState)
        }
    }
}

@Composable
private fun ChatList(
    chatMessage: List<ChatMessage>,
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
    chatMessage: ChatMessage
){
    val isModelMessage = chatMessage.participant == Participant.MODEL ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor = when (chatMessage.participant) {
        Participant.MODEL -> DarkGreen
        Participant.USER -> LightBlue
        Participant.ERROR -> MaterialTheme.colorScheme.errorContainer
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
//        Text(
//            text = chatMessage.participant.name,
//            style = MaterialTheme.typography.bodySmall,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
        Row {
            if (chatMessage.isPending) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    Text(
                        style = TextStyle(color = WhiteTextColor),
                        text = chatMessage.text,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        if(chatMessage.selectedImages.isNotEmpty()){
            LazyRow(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                items(chatMessage.selectedImages.size) { index ->
                    LoadImage(chatMessage.selectedImages[index])
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    onReasonClicked: (String, List<Uri>?) -> Unit = { _, _ -> },
    resetScroll: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            imageUris.add(it)
        }
    }

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
                IconButton(
                    onClick = {
                        if (userMessage.isNotBlank()) {
                            onReasonClicked(userMessage, if(imageUris.isNotEmpty()) imageUris.toList() else null)
                            resetScroll()
                            userMessage = ""
                            imageUris.clear()
                        }
                    },
                ) {
                    Icon(
                        Icons.Outlined.Send,
                        contentDescription = "",
                        tint = if(userMessage.isNotBlank()) Color.White else GreyBlue,
                    )
                }
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .background(LightBlue, CircleShape)
                        .padding(4.dp)
                        .clickable {
                            pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(30.dp))
        )

        if(imageUris.isNotEmpty()){
            LazyRow(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                items(imageUris.size) { index ->
                   LoadImage(image = imageUris[index])
                }
            }
        }
    }
}

@Composable
private fun LoadImage(image: Any) {
    val showDialog = remember {
        mutableStateOf(false)
    }
    if(showDialog.value){
        DialogShowImage(setShowDialog = { showDialog.value = it}, image = image)
    }

    AsyncImage(
        model = image,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .size(80.dp)
            .clickable {
                showDialog.value = true
            }
    )
}


