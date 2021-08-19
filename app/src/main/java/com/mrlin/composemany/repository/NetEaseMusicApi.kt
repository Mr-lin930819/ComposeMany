package com.mrlin.composemany.repository

import com.mrlin.composemany.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/*********************************
 * 网易云音乐API
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
interface NetEaseMusicApi {
    @GET("/login/refresh")
    fun refreshLogin(): Call<Unit>

    @GET("/login/cellphone")
    fun cellphoneLogin(@Query("phone") phone: String, @Query("password") password: String): Call<User>
}