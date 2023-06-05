package com.example.eatit.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun EatItImageCircle(photo: String) {
    if (photo != "") {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photo))
                .crossfade(true)
                .build(),
            contentDescription = "image of the restaurant",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                ),
            alpha = 0.8f,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun EatItImage(photo: String) {
    if (photo != "") {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photo))
                .crossfade(true)
                .build(),
            contentDescription = "image of the restaurant",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                },
            contentScale = ContentScale.FillWidth,
        )

    }
}