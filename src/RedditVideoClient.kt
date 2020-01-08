import api.MetaTVApi
import extensions.createRestApi
import kotlinx.coroutines.runBlocking

fun main() {
    val api: MetaTVApi = createRestApi(MetaTVApi.META_TV_URL, defaulOkHttpClient)
    runRedditParse(api)
}

private fun runRedditParse(api: MetaTVApi) {
    runBlocking {
        val subRedditVideos = api.getSubRedditVideos("lofi", "top")
        subRedditVideos.items.forEach {
            println(it)
            println()
        }
    }
}
