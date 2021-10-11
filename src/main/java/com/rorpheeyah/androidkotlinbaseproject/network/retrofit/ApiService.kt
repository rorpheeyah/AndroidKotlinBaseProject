package com.rorpheeyah.androidkotlinbaseproject.network.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/*
        _          _ ____                  _
       / \   _ __ (_) ___|  ___ _ ____   _(_) ___ ___
      / _ \ | '_ \| \___ \ / _ \ '__\ \ / / |/ __/ _ \
     / ___ \| |_) | |___) |  __/ |   \ V /| | (_|  __/
    /_/   \_\ .__/|_|____/ \___|_|    \_/ |_|\___\___|
            |_|
 */

interface ApiService {

    @GET
    suspend fun getRequestMessage(@Url url: String): Response<String>

    @GET
    suspend fun getRequestMessage(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @QueryMap requestData: Map<String, String>
    ): Response<String>

    @FormUrlEncoded
    @POST
    suspend fun postRequestMessage(
        @Url url: String,
        @FieldMap requestData: Map<String, String>
    ): Response<String>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun postRequestMessageBody(
        @Url url: String,
        @Body requestData: Map<String, String>
    ): Response<String>

    @POST
    suspend fun postRequestMessageBody(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestData: RequestBody
    ): Response<String>

    @Multipart
    @POST
    suspend fun postRequestMultipart(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @PartMap requestData: Map<String, MultipartBody>
    ): Response<String>

    @PUT
    suspend fun putRequestMessageBody(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestData: RequestBody
    ): Response<String>

    @Multipart
    @PUT
    suspend fun putRequestMultipart(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @PartMap requestData: Map<String, MultipartBody>
    ): Response<String>

    @PATCH
    suspend fun patchRequestMessageBody(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestData: RequestBody
    ): Response<String>
}