package com.example.flats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.flats.ui.navigation.NavGraph
import com.example.flats.ui.theme.FlatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlatsTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}