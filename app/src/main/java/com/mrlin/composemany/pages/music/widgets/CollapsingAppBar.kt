package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*********************************
 * 可折叠工具栏
 * @author mrlin
 * 创建于 2021年09月03日
 ******************************** */

/**
 * 关于内嵌滚动实现折叠工具栏，参考的官网例子：
 * [https://developer.android.google.cn/reference/kotlin/androidx/compose/ui/input/nestedscroll/package-summary]
 */
@Composable
private fun PlayListAppBar(
    title: String, expandedHeight: Dp? = null, bottom: (@Composable () -> Unit)? = null,
    background: (@Composable BoxScope.() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val toolbarHeight = 48.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val expandedHeightPx =
        with(LocalDensity.current) { expandedHeight?.roundToPx()?.toFloat() ?: toolbarHeightPx }
    val toolbarExpandedHeightPx = remember { mutableStateOf(expandedHeightPx) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                val newExpendedHeight = toolbarExpandedHeightPx.value + delta
                toolbarExpandedHeightPx.value =
                    newExpendedHeight.coerceIn(toolbarHeightPx, expandedHeightPx)
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-expandedHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    val percent = toolbarOffsetHeightPx.value.plus(expandedHeightPx) / expandedHeightPx
    val tbHeight = with(LocalDensity.current) { (toolbarExpandedHeightPx.value).toDp() }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        content?.invoke()
        Surface(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
//                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
        ) {
            Box {
                background?.let {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(percent), content = it
                    )
                }
                Column(
                    modifier = Modifier
                        .height(tbHeight)
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        //标题栏
                        Text(
                            title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(1 - percent)
                        )
                    }
                    Box(modifier = Modifier.weight(1.0f))
                    //底栏
                    bottom?.invoke()
                }
            }
        }
    }
}