package com.mrlin.composemany.pages.music.playsong

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
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
import javax.inject.Inject

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

    var commentsPager = Pager(
        PagingConfig(pageSize = 20)
    ) {
        CommentsPagingSource(musicApi, _song?.id ?: 0, _commentSortType.value, _commentCount)
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

        }
    }

    private class CommentsPagingSource(
        private val musicApi: NetEaseMusicApi,
        private val songId: Long,
        private val rankType: CommentData.SortType,
        private val commentCount: MutableStateFlow<Int>
    ) : PagingSource<Int, Comment>() {
        private var lastCursor: Long? = null
        override fun getRefreshKey(state: PagingState<Int, Comment>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
            val pageNum = params.key ?: 1
            val response = musicApi.commentData(
                songId,
                pageNo = pageNum,
                pageSize = params.loadSize,
                cursor = lastCursor,
                sortType = rankType
            ).await()
            commentCount.value = response.data.totalCount
            if (rankType == CommentData.SortType.NEWEST) {
                lastCursor = response.data.comments.lastOrNull()?.time
            }
            return LoadResult.Page(
                data = response.data.comments,
                prevKey = null,
                nextKey = if (response.data.hasMore) pageNum + 1 else null
            )
        }
    }
}