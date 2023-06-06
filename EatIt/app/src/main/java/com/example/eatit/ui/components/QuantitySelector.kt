package com.example.eatit.ui.components


import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun QuantitySelector(
    count: Int,
    decreaseItemCount: () -> Unit,
    increaseItemCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = "Qty",
            modifier = Modifier.align(Alignment.CenterVertically),
        )
        IconButton(
            onClick = decreaseItemCount
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Drop-Down Arrow"
            )
        }
        Text(
            text = count.toString(),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
        IconButton(
            onClick = increaseItemCount
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Drop-Down Arrow"
            )
        }
    }
}

@Preview("default")
@Composable
fun QuantitySelectorPreview() {
    QuantitySelector(1, {}, {})
}