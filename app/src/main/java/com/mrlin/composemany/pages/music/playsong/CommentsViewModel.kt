package com.mrlin.composemany.pages.music.playsong

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.entity.Comment
import com.mrlin.composemany.repository.entity.CommentData
import com.mrlin.composemany.repository.entity.FloorCommentData
import com.mrlin.composemany.repository.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.await
import retrofit2.awaitResponse
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * 评论列表
 */
@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val musicApi: NetEaseMusicApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _commentCount = MutableStateFlow(0)
    private val _commentSortType = MutableStateFlow(CommentData.SortType.RECOMMEND)
    private val _floorComment = MutableStateFlow(FloorCommentData())
    private val _replyToComment = MutableStateFlow<Comment?>(null)

    val commentCount: StateFlow<Int> = _commentCount
    val commentSortType: StateFlow<CommentData.SortType> = _commentSortType
    val floorComment: StateFlow<FloorCommentData> = _floorComment
    val replyToComment: StateFlow<Comment?> = _replyToComment
    private val _song = savedStateHandle.get<Song>("song")
    private val commentCache: MutableList<Comment> = mutableListOf()
    private var _currentSource: MemoryCachePagingSource? = null

    @OptIn(ExperimentalPagingApi::class)
    var commentsPager = Pager(
        PagingConfig(pageSize = 10),
        remoteMediator = CommentsRemoteMediator(
            musicApi,
            _song?.id ?: 0,
            _commentCount,
            commentCache
        )
    ) {
        MemoryCachePagingSource(commentCache).also {
            _currentSource = it
        }
    }

    fun changeRankType(commentRankType: CommentData.SortType) {
        _commentSortType.value = commentRankType
    }

    /**
     * 载入楼层回复评论
     */
    fun loadFloorReply(commentId: Long) = viewModelScope.launch {
        try {
            _floorComment.value = FloorCommentData()
            val floorComment = musicApi.floorComment(commentId, _song?.id ?: 0).await()
            if (floorComment.code == 200) {
                _floorComment.value = floorComment.data
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * 点赞/取消点赞主楼评论
     */
    fun toggleMainCommentLike(comment: Comment) = viewModelScope.launch {
        try {
            val like = !comment.liked
            likeComment(comment, like)
            _currentSource?.invalidate()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * 点赞/取消点赞楼层中评论
     */
    fun toggleFloorCommentLike(comment: Comment) = viewModelScope.launch {
        try {
            val like = !comment.liked
            likeComment(comment, like)
            _floorComment.value = _floorComment.value.run {
                FloorCommentData(
                    hasMore, totalCount, Date().time, comments, ownerComment
                )
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * 点赞/取消点赞
     */
    @Throws(Throwable::class)
    private suspend fun likeComment(comment: Comment, isLike: Boolean) {
        val response =
            musicApi.likeComment(_song?.id ?: 0L, comment.commentId, if (isLike) 1 else 0)
                .awaitResponse()
        if (response.isSuccessful) {
            comment.liked = isLike
            if (isLike) {
                comment.likedCount++
            } else {
                comment.likedCount--
            }
        } else {
            throw Throwable("点赞/取消点赞失败！")
        }
    }

    //发表评论
    fun publishComment(content: String) = viewModelScope.launch {
        try {
            val op = if (_replyToComment.value == null) CommentData.Op.PUBLISH else CommentData.Op.REPLY
            operateComment(op, content = content, commentId = _replyToComment.value?.commentId)
            if (op == CommentData.Op.PUBLISH) {
                _commentCount.value++
            } else {
                commentCache.find { it.commentId == _replyToComment.value?.commentId }?.showFloorComment?.let {
                    it.replyCount++
                }
            }
            _currentSource?.invalidate()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    //删除评论
    fun deleteComment(commentId: Long?) = viewModelScope.launch {
        try {
            operateComment(CommentData.Op.DELETE, commentId = commentId)
            _commentCount.value--
            _currentSource?.invalidate()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    //删除楼层评论
    fun deleteFloorComment(commentId: Long?) = viewModelScope.launch {
        try {
            operateComment(CommentData.Op.DELETE, commentId = commentId)
            _floorComment.value = _floorComment.value.run {
                val commentList = comments.toMutableList()
                commentList.removeAll { it.commentId == commentId }
                FloorCommentData(
                    hasMore, totalCount - 1, Date().time, commentList.toList(), ownerComment
                )
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    //更改回复的评论对象
    fun changeReplyTo(comment: Comment?) {
        _replyToComment.value = comment
    }

    //操作评论
    @Throws(Throwable::class)
    private suspend fun operateComment(
        op: CommentData.Op = CommentData.Op.PUBLISH,
        content: String? = null,
        commentId: Long? = null
    ) {
        val response = musicApi.comment(
            operation = op, id = _song?.id ?: 0L, content = content,
            commentId = commentId
        ).awaitResponse()
        if (response.isSuccessful) {
            if (op == CommentData.Op.PUBLISH) {
                response.body()?.comment?.let { commentCache.add(0, it) }
            } else if (op == CommentData.Op.DELETE) {
                commentCache.removeAll { it.commentId == commentId }
            }
        } else {
            throw Throwable("评论操作失败")
        }
    }

    /**
     * 缓存数据用于界面显示
     */
    private class MemoryCachePagingSource(
        val commentCache: MutableList<Comment>
    ) : PagingSource<Int, Comment>() {
        override fun getRefreshKey(state: PagingState<Int, Comment>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
            val pageNum = params.key ?: 1
            val pageSize = 10
            val pageStartIndex = (pageNum - 1) * pageSize
            val (endIndex, nextKey) = if (pageSize * pageNum > commentCache.size) {
                Pair(commentCache.size, null)
            } else {
                Pair(pageNum * pageSize, pageNum + 1)
            }
            return LoadResult.Page(
                data = ArrayList(
                    commentCache.subList(
                        pageStartIndex,
                        endIndex.coerceAtLeast(pageStartIndex)
                    )
                ),
                prevKey = null,
                nextKey = nextKey
            )
        }

        override val keyReuseSupported: Boolean
            get() = true
    }

    /**
     * 加载云端评论数据
     */
    @ExperimentalPagingApi
    private inner class CommentsRemoteMediator(
        private val musicApi: NetEaseMusicApi,
        private val songId: Long,
        private val commentCount: MutableStateFlow<Int>,
        private val commentCache: MutableList<Comment>,
    ) : RemoteMediator<Int, Comment>() {
        private var lastCursor: Long? = null
        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, Comment>
        ): MediatorResult {
            val pageNum = when (loadType) {
                LoadType.REFRESH -> 1
                else -> state.pages.size + 1
            }
            val response = musicApi.commentData(
                songId,
                pageNo = pageNum,
                pageSize = state.config.pageSize,
                cursor = lastCursor,
                sortType = _commentSortType.value
            ).await()
            commentCount.value = response.data.totalCount
            if (_commentSortType.value == CommentData.SortType.NEWEST) {
                lastCursor = response.data.comments.lastOrNull()?.time
            }
            when (loadType) {
                LoadType.REFRESH -> {
                    commentCache.clear()
                    commentCache.addAll(response.data.comments)
                    _currentSource?.invalidate()
                }
                LoadType.APPEND -> {
                    commentCache.addAll(response.data.comments)
                    _currentSource?.invalidate()
                }
                else -> {
                }
            }
            return MediatorResult.Success(
                endOfPaginationReached = !response.data.hasMore
            )
        }

    }
}