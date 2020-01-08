package routes

import api.RedditApi
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import models.reddit.CondensedVideoItem.Companion.create
import models.reddit.SubRedditVideosResponse
import models.reddit.blankCondensedVideoItem

fun Routing.reddit(redditApi: RedditApi) {
    route("subreddit") {
        get("videos") {
            val requestedSubreddit: String = call.request.queryParameters["subreddit"] ?: ""
            val subredditJson = redditApi.getSubredditJson(requestedSubreddit)
            val subRedditData = subredditJson.data
            val children = subRedditData.children.map { create(subRedditData.after ?: "", it.data) }
                .filter { it != blankCondensedVideoItem }

            call.respond(SubRedditVideosResponse(children))
        }
        get("top") {
            val requestedSubreddit: String = call.request.queryParameters["subreddit"] ?: ""
            val subredditJson = redditApi.getSubredditJson(requestedSubreddit, sort = "top")
            val subRedditData = subredditJson.data
            val children = subRedditData.children.map { create(subRedditData.after ?: "", it.data) }
                .filter { it != blankCondensedVideoItem }

            call.respond(SubRedditVideosResponse(children))
        }
    }
}
