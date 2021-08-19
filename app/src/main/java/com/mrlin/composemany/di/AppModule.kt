package com.mrlin.composemany.di

import com.mrlin.composemany.repository.NetEaseMusicApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideNetEaseMusicRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://mrlin-netease-cloud-music-api-iota-silk.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideNetEaseMusicApi(@NetEaseMusicRetrofit retrofit: Retrofit): NetEaseMusicApi =
        retrofit.create(NetEaseMusicApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetEaseMusicRetrofit