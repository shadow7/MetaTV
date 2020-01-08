package models.reddit

import models.reddit.RedditSecureMediaType.SOUNDCLOUD
import models.reddit.RedditSecureMediaType.YOUTUBE

val blankCondensedVideoItem = CondensedVideoItem("", "", "", "", "", "","")

data class CondensedVideoItem(
    val title: String,
    val url: String,
    val id: String,
    val html: String,
    val thumbnailUrl: String,
    val permalink: String,
    val after: String
) {

    companion object {
        fun create(after: String, post: SubRedditChildItemData): CondensedVideoItem {
            val embeddedContent = post.secure_media?.oembed
            val secureMediaUrl = post.secure_media_embed?.media_domain_url
            // Check that we have a minimal amount of content, the above two values
            if (embeddedContent == null && secureMediaUrl.isNullOrBlank()) return blankCondensedVideoItem
            val html = embeddedContent?.html ?: return blankCondensedVideoItem

            val link: String = when (RedditSecureMediaType.byValue(post.secure_media.type ?: "")) {
                SOUNDCLOUD -> {
                    secureMediaUrl ?: return blankCondensedVideoItem
                }
                YOUTUBE -> {
                    val youtubeLinkStart = html.substringAfter("src=\"")
                    youtubeLinkStart.substringBefore("?")
                }
                else -> return blankCondensedVideoItem
            }

            return CondensedVideoItem(
                embeddedContent.title ?: "",
                link,
                link.substringAfterLast("/"),
                html,
                embeddedContent.thumbnail_url ?: "",
                post.permalink,
                after
            )
        }
    }
}

enum class RedditSecureMediaType(val value: String) {
    SOUNDCLOUD("soundcloud.com"),
    YOUTUBE("youtube.com");

    companion object {
        fun byValue(input: String): RedditSecureMediaType? {
            return values().firstOrNull { input == it.value }
        }
    }
}