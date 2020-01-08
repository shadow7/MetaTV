package models.reddit

data class SubRedditChildItemData(
    val permalink: String = "",
    val secure_media: SecureMedia?,
    val secure_media_embed: SecureMediaEmbed?,
    val url: String = ""
)