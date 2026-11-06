package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.AlSaifApp
import com.example.ui.AlSaifViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AlSaifViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlSaifApp(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
