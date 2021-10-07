package com.mrlin.composemany.repository.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlin.composemany.repository.entity.User

/*********************************
 * 用户数据库操作
 * @author mrlin
 * 创建于 2021年08月20日
 ******************************** */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM User")
    suspend fun findUser(accountId: Long): PagingSource<Int, User>
}