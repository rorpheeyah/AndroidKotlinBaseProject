package com.rorpheeyah.androidkotlinbaseproject.network.retrofit

import com.rorpheeyah.androidkotlinbaseproject.network.exceptions.traceErrorException
import com.rorpheeyah.androidkotlinbaseproject.util.AppConstants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

/*

 ____      _              __ _ _   _   _ _   _         ____                            _
|  _ \ ___| |_ _ __ ___  / _(_) |_| | | | |_| |_ _ __ |  _ \ ___  __ _ _   _  ___  ___| |_
| |_) / _ \ __| '__/ _ \| |_| | __| |_| | __| __| '_ \| |_) / _ \/ _` | | | |/ _ \/ __| __|
|  _ <  __/ |_| | | (_) |  _| | |_|  _  | |_| |_| |_) |  _ <  __/ (_| | |_| |  __/\__ \ |_
|_| \_\___|\__|_|  \___/|_| |_|\__|_| |_|\__|\__| .__/|_| \_\___|\__, |\__,_|\___||___/\__|
                                                |_|                 |_|

 */
class RetrofitHttpRequest(private val scope: CoroutineScope, apiResponse: ApiResponse){
    private val timeOut = 30L
    private var onResponse: ApiResponse = apiResponse

    private fun createOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.API_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create()).build()
    }

    private fun createService(): ApiService {
        return createRetrofit().create(ApiService::class.java)
    }

    fun requestData(obj: JSONObject){
        val params = HashMap<String, String>()
        params["JSONData"] = obj.toString()

        postRequestMessageBody(AppConstants.API_BASE_URL + "/posts", params)
    }

    fun requestData(){
        getRequestMessage()
    }

    private fun getRequestMessage(){
        scope.launch {
            withContext(Dispatchers.Main){
                try {
                    val response = createService().getRequestMessage(AppConstants.API_BASE_URL + "/posts")
                    if(response.isSuccessful && response.body() != null){
                        onResponse.onSuccess(response.body()!!)
                    }
                }catch (e: CancellationException) {
                    onResponse.onError(traceErrorException(e))
                }catch (e: Exception) {
                    onResponse.onError(traceErrorException(e))
                }
            }
        }
    }

    private fun postRequestMessageBody(url: String, param: Map<String, String>){
        scope.launch {
            withContext(Dispatchers.Main){
                try {
                    val response = createService().postRequestMessage(url, param)
                    if(response.isSuccessful && response.body() != null){
                        onResponse.onSuccess(response.body()!!)
                    }
                }catch (e: CancellationException) {
                    onResponse.onError(traceErrorException(e))
                }catch (e: Exception) {
                    onResponse.onError(traceErrorException(e))
                }
            }
        }
    }

    companion object{

        fun jsonToList(@NotNull list: String, type: Type): List<Type>? {
            return try {
                val moshi = Moshi.Builder().build()
                val types = Types.newParameterizedType(List::class.java, type)
                val adapter: JsonAdapter<List<Type>> = moshi.adapter(types)
                return adapter.fromJson(list)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Convert any object to json
         */
        fun objectToJson(any: Any): String?{
            return try {
                val moshi = Moshi.Builder().build()
                var adapter: JsonAdapter<Any> = moshi.adapter(Any::class.java)
                val source = okio.Buffer().writeUtf8(adapter.toJson(any))
                val reader = JsonReader.of(source)
                val value = reader.readJsonValue()
                adapter = moshi.adapter(Any::class.java).indent("    ")
                adapter.toJson(value).replace("\\\"", "")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Convert any json to Object
         */
        fun jsonToObject(@NotNull json: String): Any? {
            return try {
                val moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<Any> = moshi.adapter(Any::class.java)
                adapter.fromJson(json) as Any
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}