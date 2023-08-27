package com.fmt.compose.eyepetizer.http

import com.fmt.compose.eyepetizer.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface IApi {

    @GET("v2/feed?num=0")
    suspend fun getDaily(): Daily

    @GET
    suspend fun getDaily(@Url nextPageUrl: String): Daily

    @GET("v4/tabs/follow")
    suspend fun getFollowList(): Follow

    @GET
    suspend fun getFollowList(@Url url: String): Follow

    @GET("v4/rankList")
    suspend fun getHotTabList(): TabListInfo

    @GET
    suspend fun getHotTabData(@Url url: String): Issue

    @GET("v4/categories")
    suspend fun getCategoryList(): List<Category>

    @GET
    suspend fun getCategoryVideoList(@Url url: String): Issue

    @GET("v3/specialTopics")
    suspend fun getTopicList(): SpecialTopics

    @GET
    suspend fun getTopicList(@Url nextPageUrl: String): SpecialTopics

    @GET("v7/information/list?vc=6030000&deviceModel=Android")
    suspend fun getNewsList(): News

    @GET
    suspend fun getNewsList(@Url url: String): News

    @GET("v7/community/tab/rec")
    suspend fun getRecommendList(): Recommend

    @GET
    suspend fun getRecommendList(@Url url: String): Recommend

    @GET("v4/video/related")
    suspend fun getRelateVideoList(@Query("id") id: Int): Issue

    @GET("v3/lightTopics/internal/{topicId}")
    suspend fun getTopicDetail(@Path("topicId") topicId: Int): TopicDetail

}