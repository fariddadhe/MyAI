package com.farid.myai.navigation

sealed class Screen (val route: String) {

    object Splash : Screen("splash_screen")

    object OnBoarding : Screen("onboarding_screen")

    object Gemini : Screen("gemini_screen")

    object ChatGpt : Screen("gpt_screen")
}