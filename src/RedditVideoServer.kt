import api.RedditApi
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import extensions.createRestApi
import extensions.okHttpClient
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.Routing
import routes.reddit
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val defaulOkHttpClient = okHttpClient()

val moshiInstance: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val redditApi: RedditApi = createRestApi(RedditApi.URL, defaulOkHttpClient)

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(ContentNegotiation) {
        moshi(moshiInstance)
    }

    install(Routing) {
        reddit(redditApi)
    }

    install(CORS){
        anyHost()
//        host("localhost:4200")
    }
}
