package api

import models.reddit.SubredditJsonWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {
    companion object {
        const val URL = "https://www.reddit.com/r/"
    }

    @GET("{subreddit}.json")
    suspend fun getSubredditJson(
        @Path("subreddit") subreddit: String,
        @Query("after") after: String = "",
        @Query("count") count: Int = 0,
        @Query("limit") limit: Int = 20
    ): SubredditJsonWrapper

    @GET("{subreddit}/{sort}/.json")
    suspend fun getSubredditJson(
        @Path("subreddit") subreddit: String,
        @Path("sort") sort: String,
        @Query("after") after: String = "",
        @Query("count") count: Int = 0,
        @Query("limit") limit: Int = 20
    ): SubredditJsonWrapper

}