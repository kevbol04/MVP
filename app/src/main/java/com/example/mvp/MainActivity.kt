package com.example.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mvp.ui.screens.login.AuthScreen
import com.example.mvp.ui.theme.MVPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLogin = { email, pass -> },
                        onRegister = { name, email, pass -> }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    MVPTheme {
        AuthScreen()
    }
}