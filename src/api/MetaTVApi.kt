package api

import models.reddit.SubRedditVideosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MetaTVApi {
    companion object {
        const val META_TV_URL = "http://0.0.0.0:8080"
    }

    @GET("/subreddit/videos")
    suspend fun getSubRedditVideos(
        @Query("subreddit") subreddit: String
    ): SubRedditVideosResponse

    @GET("/subreddit/videos")
    suspend fun getSubRedditVideos(
        @Query("subreddit") subreddit: String,
        @Query("sort") sort: String
    ): SubRedditVideosResponse
}