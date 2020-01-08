package extensions

import kotlinx.coroutines.suspendCancellableCoroutine
import moshiInstance
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun okHttpClient(connectionTimeout: Long = 30L): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
        .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
        .readTimeout(connectionTimeout, TimeUnit.SECONDS)
        .build()
}

inline fun <reified T : Any> createRestApi(baseUrl: String, client: OkHttpClient): T {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshiInstance))
        .build()
        .create()
}

@Suppress("UNCHECKED_CAST")
suspend fun okhttp3.Call.await(
): okhttp3.ResponseBody? = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            when (response.code()) {
                200 -> cont.resume(response.body()) // OK
                else -> cont.resumeWithException(ErrorResponse(response))
            }
        }

        override fun onFailure(call: Call, t: IOException) {
            cont.resumeWithException(t)
        }
    })
    cont.invokeOnCancellation {
        cancel()
    }
}

class ErrorResponse(response: Response) : Exception(
    "Failed with ${response.code()}: ${response.message()}\n${response.body()}"
)

inline fun <reified A : Any> Retrofit.create(): A = create(A::class.java)