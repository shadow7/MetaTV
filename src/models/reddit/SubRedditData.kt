package models.reddit

data class SubRedditData(
    val after: String?,
    val before: String?,
    val children: List<SubRedditChildItem>,
    val dist: Int,
    val modhash: String?
)