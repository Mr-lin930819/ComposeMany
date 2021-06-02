package com.mrlin.composemany.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitleRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) = Row(
    modifier = modifier
        .fillMaxWidth()
        .padding(8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    content = content
)

@Composable
fun TopCard(content: @Composable () -> Unit) = Card(
    Modifier
        .fillMaxWidth()
        .padding(8.dp),
    shape = MaterialTheme.shapes.medium.copy(all = CornerSize(10.dp)),
    content = content
)