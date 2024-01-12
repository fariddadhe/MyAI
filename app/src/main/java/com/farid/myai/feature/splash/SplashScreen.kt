package com.farid.myai.feature.splash

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.farid.myai.R
import com.farid.myai.navigation.Screen
import com.farid.myai.ui.theme.PrimaryColor
import com.farid.myai.ui.theme.WhiteTextColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(PrimaryColor),
    contentAlignment = Alignment.Center
) {
    Image(
        painter = painterResource(id = R.drawable.chatbot),
        contentDescription = ""
    )

    LaunchedEffect(key1 = true){
        delay(1500)
        navController.navigate(Screen.OnBoarding.route){
            popUpTo(Screen.Splash.route){
                inclusive = true
                saveState = true
            }
        }
    }
}



