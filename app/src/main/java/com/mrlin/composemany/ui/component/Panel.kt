package com.mrlin.composemany.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TitleDivider(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(Modifier.weight(1.0f))
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(8.dp)
        )
        Divider(Modifier.weight(1.0f))
    }
}

@Preview(showBackground = true)
@Composable
fun TitleDividerPreview() {
    TitleDivider(title = "测试")
}