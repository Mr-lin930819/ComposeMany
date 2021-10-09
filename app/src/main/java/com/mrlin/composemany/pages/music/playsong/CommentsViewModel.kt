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

    val commentCount: StateFlow<Int> = _commentCount
    val commentSortType: StateFlow<CommentData.SortType> = _commentSortType
    val floorComment: StateFlow<FloorCommentData> = _floorComment
    private val _song = savedStateHandle.get<Song>("song")
    private val commentCache: MutableList<Comment> = mutableListOf()
    private var _currentSource: MemoryCachePagingSource? = null

    @OptIn(ExperimentalPagingApi::class)
    var commentsPager = Pager(
        PagingConfig(pageSize = 10),
        remoteMediator = CommentsRemoteMediator(
            musicApi,
            _song?.id ?: 0,
            _commentSortType.value,
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
        val response = musicApi.likeComment(_song?.id ?: 0L, comment.commentId, if (isLike) 1 else 0).awaitResponse()
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
                data = ArrayList(commentCache.subList(pageStartIndex, endIndex.coerceAtLeast(pageStartIndex))),
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
        private val rankType: CommentData.SortType,
        private val commentCount: MutableStateFlow<Int>,
        private val commentCache: MutableList<Comment>,
    ) : RemoteMediator<Int, Comment>() {
        private var lastCursor: Long? = null
        override suspend fun load(loadType: LoadType, state: PagingState<Int, Comment>): MediatorResult {
            val pageNum = state.pages.size + 1
            val response = musicApi.commentData(
                songId,
                pageNo = pageNum,
                pageSize = state.config.pageSize,
                cursor = lastCursor,
                sortType = rankType
            ).await()
            commentCount.value = response.data.totalCount
            if (rankType == CommentData.SortType.NEWEST) {
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