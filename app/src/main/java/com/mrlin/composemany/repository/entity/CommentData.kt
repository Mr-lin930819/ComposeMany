package com.mrlin.composemany.repository.entity

import com.google.gson.annotations.SerializedName

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
    val topComments: List<Comment> = emptyList(),
    val hotComments: List<Comment> = emptyList(),
)

data class CommentResponse(
    val code: Int = 0,
    val data: CommentData,
)

data class CommentData(
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val cursor: Any? = null,
) {
    enum class Type {
        @SerializedName("0")
        SONG,

        @SerializedName("1")
        MV,

        @SerializedName("2")
        PLAY_LIST,
    }

    enum class SortType {
        @SerializedName("1")
        RECOMMEND,

        @SerializedName("2")
        HOT,

        @SerializedName("3")
        NEWEST,
    }
}

data class Comment(
    val user: CommentUser,
    val content: String = "",
    val time: Long = 0,
    val likedCount: Int = 0,
    val showFloorComment: FloorComment? = null,
    val tag: Tag? = null,
) {
    data class Tag(
        val datas: List<TagData>? = null,
    )

    data class TagData(
        val text: String = "",
    )
}

data class FloorComment(
    val replyCount: Long = 0,
    val showReplyCount: Boolean = false,
)

data class CommentUser(
    val nickname: String = "",
    val userId: Long = 0,
    val avatarUrl: String? = null,
)
