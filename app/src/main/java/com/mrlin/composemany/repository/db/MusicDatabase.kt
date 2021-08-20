package com.mrlin.composemany.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mrlin.composemany.model.User

/*********************************
 * 音乐功能数据库
 * @author mrlin
 * 创建于 2021年08月20日
 ******************************** */
@Database(version = 1, exportSchema = true, entities = [
    User::class
])
abstract class MusicDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}