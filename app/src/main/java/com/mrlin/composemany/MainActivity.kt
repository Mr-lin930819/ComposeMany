package com.mrlin.composemany

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrlin.composemany.fund.FundActivity
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeManyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    viewModel.runTimer()
                    Greeting("Android", viewModel.time, viewModel.menuList(), onMenuClick = { menu ->
                        when (menu) {
                            is MainViewModel.Menu.Fund -> startActivity(Intent(this, FundActivity::class.java))
                            is MainViewModel.Menu.NetEaseMusic -> TODO("无界面")
                        }
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Greeting(
    name: String,
    dataTimeData: StateFlow<String>,
    menuList: List<MainViewModel.Menu>,
    onMenuClick: ((MainViewModel.Menu) -> Unit)? = null,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val composableScope = rememberCoroutineScope()
    val dateTime: String by dataTimeData.collectAsState("")
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
    }, scaffoldState = scaffoldState, backgroundColor = Color.LightGray.copy(alpha = 0.3f)) {
        LazyVerticalGrid(cells = GridCells.Fixed(count = 3)) {
            items(menuList) {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth(0.8f)
                        .padding(8.dp)
                        .clickable { onMenuClick?.invoke(it) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(id = R.drawable.discuss), contentDescription = "")
                    Text(text = it.name)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeManyTheme {
        Greeting(
            "Android", MutableStateFlow("2020-10-10 10:00:00"), listOf(
                MainViewModel.Menu.Fund()
            )
        )
    }
}