package com.fmt.compose.eyepetizer.view

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun TopTitleAppBar(title: String, onBack: () -> Unit) {
    TopAppBar(title = {
        Text(text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
    }, backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
            }
        })
}