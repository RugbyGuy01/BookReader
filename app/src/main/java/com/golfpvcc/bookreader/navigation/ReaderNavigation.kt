package com.golfpvcc.bookreader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.golfpvcc.bookreader.screens.ReaderSplashScreen
import com.golfpvcc.bookreader.screens.details.BookDetailsScreen
import com.golfpvcc.bookreader.screens.home.Home
import com.golfpvcc.bookreader.screens.login.ReaderLoginScreen
import com.golfpvcc.bookreader.screens.search.SearchScreen
import com.golfpvcc.bookreader.screens.stats.ReaderStatsScreen
import com.golfpvcc.bookreader.screens.update.BookUpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name ){
        composable(ReaderScreens.SplashScreen.name){
            ReaderSplashScreen(navController = navController)
        }
        composable(ReaderScreens.LoginScreen.name){
            ReaderLoginScreen(navController = navController)
        }
        composable(ReaderScreens.ReaderHomeScreen.name){
            Home(navController = navController)
        }
        composable(ReaderScreens.SearchScreen.name){
            SearchScreen(navController = navController)
        }
        composable(ReaderScreens.DetailScreen.name){
            BookDetailsScreen(navController = navController)
        }
        composable(ReaderScreens.UpdateScreen.name){
            BookUpdateScreen(navController = navController)
        }
        composable(ReaderScreens.ReaderStatsScreen.name){
            ReaderStatsScreen(navController = navController)
        }
    }
}