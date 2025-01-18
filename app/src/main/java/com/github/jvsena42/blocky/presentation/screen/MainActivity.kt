package com.github.jvsena42.blocky.presentation.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.jvsena42.blocky.presentation.screen.home.ScreenHome
import com.github.jvsena42.blocky.presentation.ui.theme.BlockyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlockyTheme {
                ScreenHome()
            }
        }
    }
}