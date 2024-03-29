package com.mrlin.composemany.pages.music.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrlin.composemany.pages.music.home.MusicHomeViewModel
import com.mrlin.composemany.utils.onHandleBack

/*********************************
 * 音乐登录界面
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
@Composable
fun MusicLogin(vm: MusicHomeViewModel? = null, onQuit: (() -> Unit)? = null) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "手机号登录") })
    }) { padding ->
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Column(modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "欢迎使用云音乐", style = TextStyle(fontSize = 34.sp))
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(value = phone,
                label = { Text(text = "手机", style = MaterialTheme.typography.body1) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { phone = it },
                modifier = Modifier.onHandleBack(onQuit))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = password,
                label = { Text(text = "密码", style = MaterialTheme.typography.body1) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { password = it },
                modifier = Modifier.onHandleBack(onQuit))
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = { vm?.login(phone, password) }, modifier = Modifier.fillMaxWidth(0.5f)) {
                Text(text = "提交")
            }
        }
    }
}

@Preview
@Composable
private fun MusicLoginPagePreview() {
    MusicLogin()
}