package com.mrlin.composemany

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mrlin.composemany.ui.component.TitleDivider
import com.mrlin.composemany.ui.component.TitleRow
import com.mrlin.composemany.ui.component.TopCard
import com.mrlin.composemany.ui.theme.ComposeManyTheme
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
                    Greeting("Android", viewModel.time)
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    dataTimeData: LiveData<String>,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val composableScope = rememberCoroutineScope()
    val dateTime: String by dataTimeData.observeAsState("")
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
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Box(
                Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.primary
                    )
            )
            TopCard {
                Row(
                    Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeadTip(Modifier.weight(1.0f), title = "稳健理财", content = "近一年3.53%")
                    Divider(
                        Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 8.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    HeadTip(Modifier.weight(1.0f), title = "进阶理财", content = "追求更高收益")
                }
            }
            TopCard {
                DetailTip()
            }
            TopCard {
                GoodOpportunityTip()
            }
            TopCard {
                FundList()
            }
            Box(Modifier.padding(horizontal = 36.dp)) {
                TitleDivider(title = "蚂蚁财富")
            }
            Text(
                text = "过往业绩不预示产品未来表现，市场有风险，投资需谨慎",
                style = MaterialTheme.typography.caption.copy(
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
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
            Image(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp),
                painter = painterResource(id = R.drawable.discuss),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun DetailTip() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            var enabled: Boolean by remember { mutableStateOf(true) }
            val alpha: Float by animateFloatAsState(if (enabled) 1f else 0.3f)
            Row(
                Modifier
                    .clickable { enabled = !enabled }
                    .graphicsLayer(alpha = alpha)
            ) {
                IndexBox(Modifier.weight(1.0f), "上证指数", 3593.61, -0.20)
                IndexBox(Modifier.weight(1.0f), "深证成指", 14905.05, 0.35)
                IndexBox(Modifier.weight(1.0f), "创业板指", 3285.36, 1.64)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Box(Modifier.weight(1.0f)) {
                    HotBox(title = "今日热点", stamp = "快讯 14-12") {
                        HotFundView()
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(Modifier.weight(1.0f)) {
                    Column {
                        HotBox(title = "基金自选") {
                            Column {
                                Text(text = "嘉实物流产业股票")
                                Text(text = "+23.43%")
                                Text(text = "近6月", style = MaterialTheme.typography.caption)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HotBox(title = "直播中") {
                            Column {
                                Text(text = "正在讲解重磅新发基金")
                                Text(text = "13.3万观看", style = MaterialTheme.typography.caption)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 热门基金
 */
@Composable
private fun HotFundView() {
    Column {
        Row(Modifier.height(50.dp)) {
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight()
                    .background(Color.Red.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(1.dp))
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight()
                    .background(Color.Red.copy(alpha = 0.3f))
            )
        }
        Spacer(modifier = Modifier.height(1.dp))
        Row(Modifier.height(50.dp)) {
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight()
                    .background(Color.Green.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(1.dp))
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight()
                    .background(Color.Green.copy(alpha = 0.3f))
            )
        }
        Text(text = "独家电报|律所主任实名举报正川股份")
    }
}

@Composable
private fun IndexBox(modifier: Modifier, indexName: String, indexValue: Double, indexRate: Double) {
    Box(modifier = modifier) {
        val color = if (indexRate < 0) Color.Green else Color.Red
        Column {
            Text(text = indexName)
            Text(
                text = indexValue.toBigDecimal().toPlainString(),
                style = LocalTextStyle.current.copy(color = color)
            )
            Text(
                text = "${indexRate.toBigDecimal().toPlainString()}%",
                style = MaterialTheme.typography.caption.copy(
                    color = color
                )
            )
        }
    }
}

@Composable
private fun HotBox(title: String, stamp: String = "", content: @Composable (BoxScope.() -> Unit)) {
    val warningColor = Color.Red.copy(alpha = 0.2f)
    val nonWarningColor = Color.LightGray.copy(alpha = 0.2f)
    val boxBackground = remember { Animatable(nonWarningColor) }
    var warning by remember { mutableStateOf(false) }
    LaunchedEffect(warning) {
        boxBackground.animateTo(
            if (warning) warningColor else nonWarningColor,
            animationSpec = tween(durationMillis = 500)
        )
    }
    Card(
        Modifier
            .fillMaxWidth()
            .clickable {
                warning = !warning
            },
        backgroundColor = boxBackground.value,
        elevation = 0.dp
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Box(Modifier.padding(vertical = 8.dp), content = content)
            if (stamp.isNotEmpty()) {
                Text(text = stamp, style = MaterialTheme.typography.caption)
            }
        }
    }
}

@Composable
private fun GoodOpportunityTip() {
    var index by remember {
        mutableStateOf(0)
    }
    Column(Modifier.padding(8.dp)) {
        TabRow(
            selectedTabIndex = index,
            modifier = Modifier.height(48.dp),
            backgroundColor = Color.White,
            contentColor = Color.White
        ) {
            listOf("新发基金", "人气好基", "高端理财").forEachIndexed { i, s ->
                Tab(
                    selected = i == index,
                    onClick = { index = i },
                    selectedContentColor = Color.Blue,
                    unselectedContentColor = Color.Black,
                    text = {
                        Text(text = s)
                    })
            }
        }
        when (index) {
            0 -> NewFund()
            else -> Text(text = "暂无此功能")
        }
    }
}

@Composable
private fun FundList() {
    Column(Modifier.fillMaxWidth()) {
        TitleRow(Modifier.background(Color(255, 242, 226))) {
            Text(text = "支付宝 金选", style = MaterialTheme.typography.h6)
            Text(
                text = "产品百里挑一 深度调研超1000次",
                style = MaterialTheme.typography.caption.copy(color = Color.Gray)
            )
        }

        Column(Modifier.fillMaxWidth()) {
            TitleRow(Modifier.padding(vertical = 8.dp)) {
                Text(text = "稳中进取", fontWeight = FontWeight.Bold)
                Text(text = "更多")
            }
            Row(Modifier.padding(8.dp)) {
                FundBox(
                    "易方达增强回报债券B", 13.18, "近一年涨跌幅", "12个月", "建议持有",
                    modifier = Modifier.weight(1.0f)
                )
                Spacer(Modifier.width(24.dp))
                FundBox(
                    "南方安泰混合A", 15.57, "近一年涨跌幅", "12个月", "建议持有",
                    modifier = Modifier.weight(1.0f)
                )
            }
        }
        Divider()
        Column(Modifier.fillMaxWidth()) {
            TitleRow(Modifier.padding(vertical = 8.dp)) {
                Text(text = "金选好基", fontWeight = FontWeight.Bold)
                Text(text = "更多")
            }
            Column(Modifier.padding(8.dp)) {
                FundBox(
                    "基金界元老，16座大奖加持", 48.23, "近一年涨跌幅", "金牛奖", "行业大奖",
                    footer = "中欧新趋势混合（LOF）A"
                )
                Spacer(Modifier.height(16.dp))
                FundBox(
                    "8年老将，稳健风格，低波动低回撤", 33.70, "近一年涨跌幅", "低波动低回撤", "产品特色",
                    footer = "鹏华盛世创新混合（LOF）"
                )
            }
        }
    }
}

@Composable
private fun FundBox(
    title: String,
    rate: Double,
    rateCaption: String,
    content: String,
    contentCaption: String,
    modifier: Modifier = Modifier,
    footer: String? = null
) {
    Column(modifier = modifier) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Row(
            Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.h6.copy(color = Color.Red).toSpanStyle()
                ) {
                    append("${rate.toBigDecimal().toPlainString()}%")
                }
                append("\n")
                withStyle(style = MaterialTheme.typography.caption.toSpanStyle()) {
                    append(rateCaption)
                }
            })
            Text(text = buildAnnotatedString {
                append(content)
                append("\n")
                withStyle(style = MaterialTheme.typography.caption.toSpanStyle()) {
                    append(contentCaption)
                }
            })
        }
        footer?.let { Text(text = it) }
    }
}

@Composable
private fun NewFund() {
    @Composable
    fun DataItem(data: String, subContent: String) {
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)) {
                append(data)
            }
            append("\n")
            withStyle(style = SpanStyle(fontSize = 12.sp)) {
                append(subContent)
            }
        }, textAlign = TextAlign.Center)
    }
    Column {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(red = 255, green = 241, blue = 242), Color.White)
                    )
                )
                .fillMaxWidth()
                .height(150.dp)
        )
        TitleDivider(title = "相关数据")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            DataItem(data = "王园园", subContent = "拟任基金经理")
            DataItem(data = "TOP3", subContent = "代表作同类业绩")
            DataItem(data = "超8700万", subContent = "团队受用户选择")
        }
        TitleDivider(title = "大咖说")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.certificate),
                contentDescription = "",
                modifier = Modifier
                    .size(56.dp, 56.dp)
                    .padding(8.dp)
            )
            Box(
                Modifier
                    .background(Color(239, 243, 255))
                    .padding(8.dp)
            ) {
                Text(text = "这支基金或将得到天惠的传承？朱少醒这样评价她...")
            }
        }
    }
}

@Preview(showBackground = true, name = "基金列表")
@Composable
fun FundListPreview() {
    FundList()
}

@Preview(showBackground = true, name = "热点面板")
@Composable
fun HotPreview() {
    HotBox(title = "今日热点", stamp = "快讯 14-12") {
        Column {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .background(Color.Red.copy(alpha = 0.3f))
            )
            Text(text = "独家电报|律所主任实名举报正川股份")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeManyTheme {
        Greeting("Android", MutableLiveData("2020-10-10 10:00:00"))
    }
}