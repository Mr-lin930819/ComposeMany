package com.mrlin.composemany.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*

/*********************************
 * Modifier类辅助
 * @author mrlin
 * 创建于 2021年10月27日
 ******************************** */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onHandleBack(onBackPressed: (() -> Unit)?): Modifier {
    return onKeyEvent {
        if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
            onBackPressed?.invoke()
        }
        true
    }
}