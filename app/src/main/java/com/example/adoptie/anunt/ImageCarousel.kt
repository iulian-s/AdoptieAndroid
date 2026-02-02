package com.example.adoptie.anunt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_IMAGE_URL

@Composable
fun ImageCarousel(imageUrls: List<String>) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = {imageUrls.size})

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
        ) { page ->
            val imageUrl = BASE_IMAGE_URL + imageUrls[page]

            val imageModifier = Modifier
                .fillMaxWidth()
                .height(200.dp)

            Box(modifier = imageModifier){
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null, // Fundalul nu are nevoie de descriere
                    modifier = Modifier
                        .fillMaxSize()
                        // blur in margini
                        .blur(radius = 24.dp),

                    contentScale = ContentScale.Crop,
                    alpha = 0.5f
                )

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagine ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ){
            repeat(imageUrls.size) { iteration ->
                val color = if(pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
    
}