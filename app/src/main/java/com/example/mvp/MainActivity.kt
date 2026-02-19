package com.example.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mvp.ui.navigation.AppNavGraph
import com.example.mvp.ui.theme.MVPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVPTheme {
                AppNavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    MVPTheme {
        AppNavGraph()
    }
}