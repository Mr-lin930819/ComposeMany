package com.mrlin.composemany.repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlin.composemany.model.User

/*********************************
 * 用户数据库操作
 * @author mrlin
 * 创建于 2021年08月20日
 ******************************** */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM User WHERE ua_id=(:accountId)")
    suspend fun findUser(accountId: Long): User?
}