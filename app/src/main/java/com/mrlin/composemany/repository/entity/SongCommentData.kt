package com.mrlin.composemany.repository.entity

/**
 * 歌曲评论
 */
data class SongCommentData(
    val isMusician: Boolean = false,
    val userId: Long = 0,
    val code: Int = 0,
    val total: Int = 0,
    val more: Boolean = false,
    val comments: List<Comment> = emptyList(),
)

data class Comment(
    val user: CommentUser,
    val content: String = "",
    val time: Long = 0,
    val likedCount: Int = 0,
)

data class BeReplied(
    val user: CommentUser,
    val beRepliedCommentId: Long = 0,
    val content: String = "",
    val status: Int = 0,
    val expressionUrl: String? = null,
)

data class CommentUser(
    val userId: Long = 0,
    val avatarUrl: String? = null,
)
