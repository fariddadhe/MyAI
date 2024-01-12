package com.farid.myai.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.farid.myai.R
import com.farid.myai.navigation.Screen
import com.farid.myai.ui.theme.GreyBlue
import com.farid.myai.ui.theme.PrimaryColor
import com.farid.myai.ui.theme.WhiteTextColor

@Composable
fun OnboardingScreen(
    navController: NavController
) = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(PrimaryColor),
    contentAlignment = Alignment.Center
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.chatbot),
            contentDescription = "",
            Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Silahkan Pilih AI Mu",
            style = TextStyle(
                color = WhiteTextColor,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        ItemAI(name = "Gemini Google", navController)
        Spacer(modifier = Modifier.height(16.dp))
        ItemAI(name = "Chat GPT", navController)
    }
}


@Composable
private fun ItemAI(
    name: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(width = 1.dp, color = GreyBlue),
                shape = CircleShape
            )
            .clickable {
                if(name == "Chat GPT"){
                    navController.navigate(Screen.ChatGpt.route)
                }else{
                    navController.navigate(Screen.Gemini.route)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = name,
            style = TextStyle(
                color = WhiteTextColor,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        )
    }
}



