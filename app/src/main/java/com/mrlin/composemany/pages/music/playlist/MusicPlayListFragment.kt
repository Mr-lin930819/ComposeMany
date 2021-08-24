package com.mrlin.composemany.pages.music.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.mrlin.composemany.pages.music.home.composeContent
import kotlin.math.roundToInt

/*********************************
 * 音乐播放列表
 * @author mrlin
 * 创建于 2021年08月23日
 ******************************** */
class MusicPlayListFragment : Fragment() {
    private val args: MusicPlayListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeContent {
        val recommend = args.recommend
        PlayListAppBar(expandedHeight = 200.dp)
    }
}

@Composable
private fun PlayListAppBar(expandedHeight: Dp? = null) {
// here we use LazyColumn that has build-in nested scroll, but we want to act like a
// parent for this LazyColumn and participate in its nested scroll.
// Let's make a collapsing toolbar for LazyColumn
    val toolbarHeight = 48.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
// our offset to collapse toolbar
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val expandedHeightPx = with(LocalDensity.current) { expandedHeight?.roundToPx()?.toFloat() ?: toolbarHeightPx}
    val toolbarExpandedHeightPx = remember { mutableStateOf(expandedHeightPx) }
// now, let's create connection to the nested scroll system and listen to the scroll
// happening inside child LazyColumn
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                val newExpendedHeight = toolbarExpandedHeightPx.value + delta
                toolbarExpandedHeightPx.value = newExpendedHeight.coerceIn(toolbarHeightPx, expandedHeightPx)
                if (newExpendedHeight < 0) {
                    toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                }
                // here's the catch: let's pretend we consumed 0 in any case, since we want
                // LazyColumn to scroll anyway for good UX
                // We're basically watching scroll without taking it
                return Offset.Zero
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            // attach as a parent to the nested scroll system
            .nestedScroll(nestedScrollConnection)
    ) {
        val tbHeight =
            with(LocalDensity.current) { (toolbarExpandedHeightPx.value).toDp() }
        // our list with build in nested scroll support that will notify us about its scroll
        LazyColumn(contentPadding = PaddingValues(top = tbHeight)) {
            items(100) { index ->
                Text(
                    "I'm item $index", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        Surface(
            modifier = Modifier
                .height(tbHeight)
                .fillMaxWidth(),
//                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
            color = MaterialTheme.colors.primary
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "toolbar offset is ${toolbarOffsetHeightPx.value}",
                    Modifier.height(toolbarHeight),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(IntrinsicSize.Max))
                MusicListHeader {

                }
            }
        }
    }
}

@Composable
private fun MusicListHeader(count: Int? = null, onTap: (() -> Unit)? = null, tail: @Composable () -> Unit) {
    Box(modifier = Modifier.height(64.dp)) {
        Row(Modifier.padding(start = 10.dp)) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(36.dp, 36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "播放全部")
            Spacer(modifier = Modifier.width(5.dp))
            count?.let { Text(text = "共${count}首") }
            Spacer(modifier = Modifier.fillMaxWidth())
            tail()
        }
    }
}