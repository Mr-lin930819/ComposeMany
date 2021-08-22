package com.mrlin.composemany.net

import android.content.Context
import androidx.datastore.core.DataStore
import com.mrlin.composemany.CookieStore
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * cookie持久化
 */
class PersistCookieJar constructor(cookieStore: DataStore<CookieStore>) : CookieJar {
    private val cookieStore = PersistentCookieStore(cookieDataStore = cookieStore)
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookieStore.add(url, it) }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore.get(url = url)
    }

}