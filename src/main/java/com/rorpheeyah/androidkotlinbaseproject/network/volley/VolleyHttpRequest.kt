package com.rorpheeyah.androidkotlinbaseproject.network.volley

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rorpheeyah.androidkotlinbaseproject.network.exceptions.volleyTraceErrorException
import com.rorp.rorpdevlibs.network.volley.OnVolleyResponse
import com.rorpheeyah.androidkotlinbaseproject.util.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull

/*

__     __    _ _            _   _ _   _         ____                            _
\ \   / /__ | | | ___ _   _| | | | |_| |_ _ __ |  _ \ ___  __ _ _   _  ___  ___| |_
 \ \ / / _ \| | |/ _ \ | | | |_| | __| __| '_ \| |_) / _ \/ _` | | | |/ _ \/ __| __|
  \ V / (_) | | |  __/ |_| |  _  | |_| |_| |_) |  _ <  __/ (_| | |_| |  __/\__ \ |_
   \_/ \___/|_|_|\___|\__, |_| |_|\__|\__| .__/|_| \_\___|\__, |\__,_|\___||___/\__|
                      |___/              |_|                 |_|
 */
open class VolleyHttpRequest(private val context: Context, private val scope: CoroutineScope, private val onResponse: OnVolleyResponse){

    companion object{
        val TAG: String = this::class.java.simpleName
    }

    fun requestData(){
        val url = AppConstants.API_BASE_URL + "/posts"

        request(url)
    }

    private fun request(@NotNull url: String){
        scope.launch {
            val queue = Volley.newRequestQueue(context)
            val stringReq = StringRequest(Request.Method.GET, url, { response ->
                onResponse.onSuccess(response.toString())
            }, {
                onResponse.onError(it)
            })
            stringReq.retryPolicy = DefaultRetryPolicy(5*1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue.add(stringReq)
        }
    }

    fun getErrorMessage(error: VolleyError?): String {
        return volleyTraceErrorException(error)
    }
}