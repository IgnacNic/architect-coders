package com.ignacnic.architectcoders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ignacnic.architectcoders.navigation.locationRequesterNode
import com.ignacnic.architectcoders.navigation.Screens
import com.ignacnic.architectcoders.navigation.locationDetailNode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screens.LocationRequester) {
                locationRequesterNode(navController)
                locationDetailNode(navController)
            }
        }
    }
}
