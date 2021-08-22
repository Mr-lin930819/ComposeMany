package com.mrlin.composemany.net

import android.text.TextUtils
import androidx.datastore.core.DataStore
import com.mrlin.composemany.CookieInfo
import com.mrlin.composemany.CookieStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieStore(private val cookieDataStore: DataStore<CookieStore>) {
    //根据各自的业务形态进行定制，可以使用hashMap，甚至也可以选用其他数据结构存储Cookie。例子中使用了HashMap实现，key作为一级域名；value则是以cookieToken为key的Cookie映射，cookieToken的获取见下述方法。
    private val cookies: MutableMap<String, ConcurrentHashMap<String, Cookie>>

    /**
     * cookieToken的获取
     */
    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name() + "@" + cookie.domain()
    }

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        if (!cookies.containsKey(url.host())) {
            cookies[url.host()] = ConcurrentHashMap<String, Cookie>()
        }
        cookies[url.host()]?.put(name, cookie)

        //讲cookies持久化到本地
        if (cookies.containsKey(url.host())) {
            runBlocking {
                cookieDataStore.updateData {
                    it.toBuilder()
                        .putHosts(url.host(), cookies[url.host()]?.keys?.joinToString(","))
                        .putCookieCache(name, CookieInfo.getDefaultInstance().fromCookie(cookie))
                        .build()
                }
            }
        }
    }

    private fun CookieInfo.fromCookie(cookie: Cookie): CookieInfo {
        return toBuilder().setName(cookie.name())
            .setValue(cookie.value())
            .setExpiresAt(cookie.expiresAt())
            .setDomain(cookie.domain())
            .setPath(cookie.path())
            .setSecure(cookie.secure())
            .setHostOnly(cookie.hostOnly())
            .setHostOnly(cookie.hostOnly())
            .setPersistent(cookie.persistent())
            .build()
    }

    private fun CookieInfo.toCookie(): Cookie = Cookie.Builder()
        .name(name)
        .value(value)
        .expiresAt(expiresAt)
        .apply { if (hostOnly) hostOnlyDomain(domain) else domain(domain) }
        .path(path)
        .apply { if (secure) secure() }
        .apply { if (httpOnly) httpOnly() }
        .build()

    operator fun get(url: HttpUrl): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host())) ret.addAll(cookies[url.host()]?.values ?: emptyList())
        return ret
    }

    fun removeAll(): Boolean {
        runBlocking {
            cookieDataStore.updateData { it.toBuilder().clear().build() }
        }
        cookies.clear()
        return true
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        return if (cookies.containsKey(url.host()) && cookies[url.host()]?.containsKey(name) == true) {
            cookies[url.host()]?.remove(name)
            runBlocking {
                cookieDataStore.updateData {
                    it.toBuilder()
                        .removeCookieCache(name)
                        .putHosts(url.host(), cookies[url.host()]?.keys?.joinToString(","))
                        .build()
                }
            }
            true
        } else {
            false
        }
    }

    init {
        cookies = ConcurrentHashMap<String, ConcurrentHashMap<String, Cookie>>()

        //将持久化的cookies缓存到内存中 即map cookies
        runBlocking {
            cookieDataStore.data.firstOrNull()?.run {
                hostsMap.forEach { entry ->
                    val cookieNames = TextUtils.split(entry.value, ",")
                    for (name in cookieNames) {
                        cookieCacheMap[name]?.let {
                            if (!cookies.containsKey(entry.key)) {
                                cookies[entry.key] = ConcurrentHashMap<String, Cookie>()
                            }
                            cookies[entry.key]?.put(name, it.toCookie())
                        }
                    }
                }
            }
        }
    }
}