package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.mrlin.composemany.R
import com.mrlin.composemany.repository.entity.limitSize
import com.mrlin.composemany.utils.simpleNumText

@Composable
fun PlayListWidget(
    text: String, picUrl: String? = null, subText: String? = null, playCount: Long? = null,
    maxLines: Int? = null, index: Int? = null, onTap: (() -> Unit)?
) {
    Column(
        Modifier
            .width(112.dp)
            .wrapContentHeight()
            .clickable(onTap != null, onClick = { onTap?.invoke() }),
    ) {
        val lines = maxLines ?: Int.MAX_VALUE
        val overflow = if (maxLines != null) TextOverflow.Ellipsis else TextOverflow.Clip
        picUrl?.run { PlayListCover(playCount = playCount, url = this) }
        index?.run { Text(text = toString()) }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = text, maxLines = lines, overflow = overflow, fontSize = 12.sp)
        subText?.run {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = this,
                style = MaterialTheme.typography.caption,
                maxLines = lines,
                overflow = overflow
            )
        }
    }
}

@Composable
fun PlayListCover(
    playCount: Long? = null, width: Float = 108f, height: Float = width, radius: Float = 24f,
    url: String? = null
) {
    Box(modifier = Modifier.clip(RoundedCornerShape(radius))) {
        Image(
            painter = rememberImagePainter("${url?.limitSize(width.toInt(), height.toInt())}"),
            contentDescription = null,
            modifier = Modifier.size(width.dp, height.dp)
        )
        playCount?.let {
            Row(Modifier.padding(top = 1.dp, end = 3.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.icon_triangle),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp, 20.dp)
                )
                Text(
                    text = it.simpleNumText(),
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00AA00)
@Composable
private fun PlayListPreview() {
    PlayListWidget(text = "推荐", subText = "子标题", picUrl = "", playCount = 1000) {

    }
}