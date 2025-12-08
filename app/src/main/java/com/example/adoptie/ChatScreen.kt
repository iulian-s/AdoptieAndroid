package com.example.adoptie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE0F7FA)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pagina Chat ", style = MaterialTheme.typography.headlineMedium)
    }
}