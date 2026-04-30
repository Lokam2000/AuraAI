package com.example.auraai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.auraai.ui.ChatScreen
import com.example.auraai.ui.theme.AuraAITheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuraAITheme {
                ChatScreen(viewModel = viewModel)
            }
        }
    }
}
