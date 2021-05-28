package com.mrlin.composemany

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorModel
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeManyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Greeting(
    name: String, scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: MainViewModel = viewModel()
) {
    val composableScope = rememberCoroutineScope()
    val dateTime: String by viewModel.time.observeAsState("")
    viewModel.runTimer()
    Scaffold(topBar = {
        TopAppBar(title = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name)
                Text(text = dateTime, style = MaterialTheme.typography.body2)
            }
        }, navigationIcon = {
            IconButton(onClick = {
                composableScope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = ""
                )
            }
        })
    }, drawerContent = {
        Text(text = "Hello!")
    }, scaffoldState = scaffoldState) {
        Column {
            Box(
                Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color.Blue
                            .convert(ColorSpaces.CieLab)
                            .copy(green = -60f)
                    )
            )
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Row {
                    HeadTip(Modifier.weight(1.0f), title = "稳健理财", content = "近一年3.53%")
                    Box(
                        Modifier
                            .wrapContentHeight()
                            .width(1.dp)
                            .background(Color.Gray.copy(alpha = 0.2f))
                    )
                    HeadTip(Modifier.weight(1.0f), title = "进阶理财", content = "追求更高收益")
                }
            }
        }
    }
}

@Composable
fun HeadTip(modifier: Modifier = Modifier, title: String = "", content: String = "") {
    Box(modifier = modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.h6)
                Text(text = content, style = MaterialTheme.typography.caption)
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = ""
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeManyTheme {
        Greeting("Android")
    }
}