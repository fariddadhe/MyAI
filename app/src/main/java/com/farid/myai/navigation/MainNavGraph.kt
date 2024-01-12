package com.farid.myai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.farid.myai.BuildConfig
import com.farid.myai.feature.gemini.ChatScreen
import com.farid.myai.feature.gemini.ChatViewModel
import com.farid.myai.feature.onboarding.OnboardingScreen
import com.farid.myai.feature.splash.SplashScreen
import com.google.ai.client.generativeai.GenerativeModel

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = "main_route",
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.OnBoarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(route = Screen.Gemini.route) {
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = BuildConfig.apiKey
            )
            val viewModel = ChatViewModel(generativeModel)
            ChatScreen(viewModel)
        }
        composable(route = Screen.ChatGpt.route) {

        }
    }
}