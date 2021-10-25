package com.mrlin.composemany.repository

import com.mrlin.composemany.repository.entity.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

/*********************************
 * 网易云音乐API
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
interface NetEaseMusicApi {
    @GET("/login/refresh")
    fun refreshLogin(): Call<Unit>

    /**
     * 手机号登录
     */
    @GET("/login/cellphone")
    fun cellphoneLogin(
        @Query("phone") phone: String,
        @Query("password") password: String
    ): Call<User>

    /**
     * 推荐歌单
     */
    @GET("/recommend/resource")
    fun recommendResource(): Call<RecommendData>

    /**
     * 新碟上架
     */
    @GET("/top/album")
    fun topAlbums(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0
    ): Call<AlbumData>

    /**
     * MV 排行
     */
    @GET("/top/mv")
    fun topMVs(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0
    ): Call<MVData>

    /**
     * banner数据
     */
    @GET("/banner")
    fun banners(@Query("type") type: Int = BannerData.TYPE_ANDROID): Call<BannerData>

    /**
     * 播放列表详情
     */
    @GET("/playlist/detail")
    fun playListDetail(@Query("id") id: Long): Call<PlayListData>

    /**
     * 获取音乐 url
     */
    @GET("/song/url")
    fun musicUrl(@Query("id") id: Long, @Query("br") br: Int = 128000): Call<MusicUrlData>

    /**
     * 获取个人歌单
     */
    @GET("/user/playlist")
    fun selfPlaylistData(@Query("uid") uid: Long): Call<MyPlayListData>

    /**
     * 歌曲评论
     */
    @GET("/comment/music")
    fun songCommentData(
        @Query("id") id: Long,
        //取出评论数量 , 默认为 20
        @Query("limit") limit: Int = 20,
        //偏移数量 , 用于分页 , 如 :( 评论页数 -1)*20, 其中 20 为 limit 的值
        @Query("offset") offset: Int
    ): Call<SongCommentData>

    /**
     * 新的评论接口
     */
    @GET("/comment/new")
    fun commentData(
        @Query("id") id: Long,
        @Query("type") type: CommentData.Type = CommentData.Type.SONG,
        @Query("pageNo") pageNo: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortType") sortType: CommentData.SortType = CommentData.SortType.RECOMMEND,
        //当sortType为3时且页数不是第一页时需传入,值为上一条数据的time
        @Query("cursor") cursor: Long? = null,
        @Query("timestamp") timestamp: Long? = Date().time
    ): Call<CommentResponse>

    /**
     * 楼层评论
     */
    @GET("/comment/floor")
    fun floorComment(
        //楼层评论 id
        @Query("parentCommentId") parentCommentId: Long,
        //资源 id
        @Query("id") id: Long,
        @Query("type") type: CommentData.Type = CommentData.Type.SONG,
        @Query("limit") limit: Int = 20,
        @Query("time") time: Long? = null,
        @Query("timestamp") timestamp: Long? = Date().time
    ): Call<FloorCommentResponse>

    /**
     * 评论点赞/取消点赞
     */
    @GET("/comment/like")
    fun likeComment(
        //资源 id, 如歌曲 id,mv id
        @Query("id") id: Long,
        //评论 id
        @Query("cid") cid: Long,
        //是否点赞 ,1 为点赞 ,0 为取消点赞
        @Query("t") isLike: Int,
        //资源类型
        @Query("type") type: CommentData.Type = CommentData.Type.SONG,
    ): Call<EmptyResponse>

    //评论操作（发布、回复、删除）
    @GET("/comment")
    fun comment(
        //0 删除,1 发送, 2 回复
        @Query("t") operation: CommentData.Op = CommentData.Op.PUBLISH,
        @Query("type") type: CommentData.Type = CommentData.Type.SONG,
        //对应资源 id
        @Query("id") id: Long,
        @Query("content") content: String? = null,
        //回复的评论id (回复评论时必填)
        @Query("commentId") commentId: Long? = null,
    ): Call<CommentOpResponse>

    //喜欢音乐
    @GET("/like")
    fun likeSong(@Query("id") id: Long, @Query("like") like: Boolean = true): Call<EmptyResponse>

    //喜欢的音乐列表
    @GET("likelist")
    fun likeList(
        @Query("uid") uid: Long,
        @Query("timestamp") timestamp: Long? = Date().time
    ): Call<LikeList>

    /**
     * 编辑歌单
     * @param [pid]    歌单 id
     * @param [tracks] 歌曲 id,可多个,用逗号隔开
     */
    @GET("/playlist/tracks")
    fun editPlayList(
        @Query("op") op: PlayList.Op = PlayList.Op.ADD,
        @Query("pid") pid: Long,
        @Query("tracks") tracks: Long
    ): Call<EmptyResponse>

    /**
     * 获取歌词
     */
    @GET("/lyric")
    fun lyric(@Query("id") id: Long): Call<LyricData>
}