package com.example.TIUMusic.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TIUMusic.ui.theme.BackgroundColor

@Composable
fun HomeScreen(

    modifier: Modifier = Modifier.background(BackgroundColor)

){
    Scaffold(
        topBar = {
            Text("Home", modifier = Modifier.padding(8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

    ) { padding ->
        val scrollState = rememberScrollState()
        Text("Top Picks For You", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            modifier = Modifier.padding(padding)
        ) {

        }


    }
}




