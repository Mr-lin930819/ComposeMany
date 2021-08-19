package com.mrlin.composemany.state

/*********************************
 * 界面状态
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
sealed class ViewState {
    object Normal : ViewState()

    class Busy(message: String? = null) : ViewState()

    class Error(val reason: String) : ViewState()
}