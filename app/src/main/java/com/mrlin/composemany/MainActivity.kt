package com.mrlin.composemany

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mrlin.composemany.pages.fund.FundActivity
import com.mrlin.composemany.pages.mall.MallActivity
import com.mrlin.composemany.pages.music.MusicSplashActivity
import com.mrlin.composemany.ui.theme.Blue500
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setStatusBarColor(color = Blue500)
                systemUiController.setNavigationBarColor(color = Color.Black)
            }
            LaunchedEffect(key1 = true, block = { viewModel.runTimer() })
            ComposeManyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Compose", viewModel.time, viewModel.menuList(), onMenuClick = { menu ->
                        when (menu) {
                            is MainMenu.Fund -> startActivity(Intent(this, FundActivity::class.java))
                            is MainMenu.NetEaseMusic -> startActivity(
                                Intent(this, MusicSplashActivity::class.java)
                            )
                            is MainMenu.Mall -> startActivity(Intent(this, MallActivity::class.java))
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun Greeting(
    name: String,
    dataTimeData: StateFlow<String>,
    menuList: List<MainMenu>,
    onMenuClick: ((MainMenu) -> Unit)? = null,
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
        Surface(
            color = MaterialTheme.colors.primary
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Text(
                    text = "Compose Many",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }, scaffoldState = scaffoldState, backgroundColor = Color.LightGray.copy(alpha = 0.3f)) {
        LazyVerticalGrid(columns = GridCells.Fixed(count = 3), Modifier.padding(it)) {
            items(menuList) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                        .clickable { onMenuClick?.invoke(it) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(id = it.icon), contentDescription = "")
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
                MainMenu.Fund(), MainMenu.Mall()
            )
        )
    }
}