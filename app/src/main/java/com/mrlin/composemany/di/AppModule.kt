package com.mrlin.composemany.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import com.mrlin.composemany.CookieStore
import com.mrlin.composemany.MusicSettings
import com.mrlin.composemany.net.PersistCookieJar
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.db.MusicDatabase
import com.mrlin.composemany.repository.store.CookieStoreSerializer
import com.mrlin.composemany.repository.store.MusicSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/*********************************
 * 应用依赖模块
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @NetEaseMusicRetrofit
    @Provides
    fun provideNetEaseMusicRetrofit(
        cookieDataStore: DataStore<CookieStore>
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://mrlin-netease-cloud-music-api-iota-silk.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(EnumRetrofitConverterFactory())
        .client(
            OkHttpClient.Builder().cookieJar(PersistCookieJar(cookieDataStore))
                .addInterceptor(OkhttpLogger).build()
        )
        .build()

    private object OkhttpLogger : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Log.d("OkhttpLogger", "raw request：${request}")
            val response = chain.proceed(request)
            Log.d("OkhttpLogger", "raw response：${response.peekBody(1024 * 10).string()}")
            return response
        }
    }

    @Singleton
    @Provides
    fun provideNetEaseMusicApi(@NetEaseMusicRetrofit retrofit: Retrofit): NetEaseMusicApi =
        retrofit.create(NetEaseMusicApi::class.java)

    @Singleton
    @Provides
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, "net-ease-music").build()

    @Provides
    fun provideMusicSettings(@ApplicationContext context: Context): DataStore<MusicSettings> =
        context.musicSettingsDataStore

    @Provides
    fun provideCookieStore(@ApplicationContext context: Context): DataStore<CookieStore> =
        context.cookieStoreDataStore

    private val Context.musicSettingsDataStore: DataStore<MusicSettings> by dataStore(
        fileName = "music_settings.pb",
        serializer = MusicSettingsSerializer
    )

    private val Context.cookieStoreDataStore: DataStore<CookieStore> by dataStore(
        fileName = "cookie_store.pb",
        serializer = CookieStoreSerializer
    )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetEaseMusicRetrofit