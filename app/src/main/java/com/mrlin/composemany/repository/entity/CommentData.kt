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

data class CommentOpResponse(
    val code: Int = 0,
    val comment: Comment,
)

data class FloorCommentResponse(
    val code: Int = 0,
    val data: FloorCommentData,
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

    enum class Op {
        @SerializedName("1")
        PUBLISH,

        @SerializedName("2")
        REPLY,

        @SerializedName("0")
        DELETE,
    }
}

/**
 * 楼层评论
 */
data class FloorCommentData(
    val hasMore: Boolean = false,
    val totalCount: Int = 0,
    val time: Long = 0L,
    var comments: List<Comment> = emptyList(),
    var ownerComment: Comment? = null,
)

data class Comment(
    val user: CommentUser,
    val content: String = "",
    val time: Long = 0,
    var likedCount: Int = 0,
    val showFloorComment: FloorComment? = null,
    val tag: Tag? = null,
    val commentId: Long = 0L,
    val beReplied: List<BeReplied>? = null,
    var liked: Boolean = false,
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

/**
 * 引用回复
 */
data class BeReplied(
    val user: CommentUser,
    val content: String? = null,
    val status: Int = 0,
    val beRepliedCommentId: Long = 0,
)
