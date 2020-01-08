import api.MetaTVApi
import extensions.createRestApi
import extensions.iframe
import extensions.returnNowPlayingListTuple
import io.kweb.Kweb
import io.kweb.dom.element.creation.ElementCreator
import io.kweb.dom.element.creation.tags.*
import io.kweb.dom.element.new
import io.kweb.plugins.fomanticUI.fomantic
import io.kweb.plugins.fomanticUI.fomanticUIPlugin
import io.kweb.state.KVar
import io.kweb.state.render.render
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.reddit.CondensedVideoItem
import kotlin.coroutines.CoroutineContext

const val DEFAULT_SUBREDDIT: String = "youtubehaiku"
val currentlyPlayingItem = KVar(
    CondensedVideoItem(
        "Rick Roll",
        "https://www.youtube.com/embed/oHg5SJYRHA0",
        "",
        "",
        "",
        "",
        ""
    )
)
val globalVideoList = KVar(listOf(currentlyPlayingItem.value))
val subredditText = KVar(DEFAULT_SUBREDDIT)
val subredditSelection = KVar(DEFAULT_SUBREDDIT)
val redditApi: MetaTVApi = createRestApi(MetaTVApi.META_TV_URL, defaulOkHttpClient)

fun main() {
    KWebApp()
}

class KWebApp : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    init {
        launch {
            val newItems = redditApi.getSubRedditVideos(DEFAULT_SUBREDDIT).items
            val firstItem = newItems[0]
            globalVideoList.value = newItems
            currentlyPlayingItem.value = firstItem
        }

        Kweb(port = 16097, plugins = listOf(fomanticUIPlugin)) {
            doc.body.new {
                div(fomantic.ui.grid).new {
                    div(fomantic.row.centered).new {
                        mainVideoContentSection()
                    }
                    previousNowAndNextContent()
                }
            }
        }
    }
}

private fun ElementCreator<DivElement>.mainVideoContentSection() {
    render(currentlyPlayingItem) { item ->
        iframe(
            mapOf(
                "width" to 1280,
                "height" to 720,
                "src" to item.url,
                "frameborder" to "0",
                "allow" to "accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture",
                "allowfullscreen" to true
            )
        ).apply {
            setAttribute("src", currentlyPlayingItem)
        }
    }
}

private fun ElementCreator<DivElement>.sortAndSearchFields() {
    h1().text("Meta TV!")
    div(fomantic.ui.form).new {
        div(fomantic.field).new {
            select(
                listOf("youtubehaiku" to "youtubehaiku", "videos" to "videos", "cringe" to "cringe"),
                attributes = fomantic.ui.fluid.dropdown
            ).apply {
                value = subredditSelection
                on.change {
                    subredditText.value = subredditSelection.value
                    GlobalScope.launch {
                        globalVideoList.value = redditApi.getSubRedditVideos(subredditText.value).items
                    }
                }
            }
        }
        div(fomantic.field).new {
            input(InputType.text, "", "", 50, "subreddit name")
                .apply {
                    value = subredditText
                    on.keypress {
                        if (it.key == "Enter") {
                            GlobalScope.launch {
                                globalVideoList.value = redditApi.getSubRedditVideos(subredditText.value).items
                            }
                        }
                    }
                }
        }
        div(fomantic.field).new {
            button(fomantic.ui.button, ButtonType.submit, false)
                .apply {
                    text = KVar("Submit")
                    on.click {
                        GlobalScope.launch {
                            globalVideoList.value = redditApi.getSubRedditVideos(subredditText.value).items
                        }
                    }
                }
        }
    }
}

private fun ElementCreator<DivElement>.previousNowAndNextContent() {
    render(currentlyPlayingItem) {
        span(fomantic.ui.three.wide.column).new {
            div(fomantic.four.column.row).new {
                globalVideoList.value.returnNowPlayingListTuple(currentlyPlayingItem.value).forEach { videoItem ->
                    div(fomantic.ui.column).new {
                        if (videoItem == null) {
                            div()
                        } else {
                            img(videoItem.thumbnailUrl, fomantic.ui.small.image).apply {
                                on.click {
                                    currentlyPlayingItem.value = videoItem
                                }
                            }
                        }
                    }
                }
                div(fomantic.right.floated.left.aligned.column).new {
                    sortAndSearchFields()
                }
            }
        }
    }
}

