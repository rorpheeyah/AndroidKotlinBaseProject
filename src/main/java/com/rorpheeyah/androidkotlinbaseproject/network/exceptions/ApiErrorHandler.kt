package com.rorpheeyah.androidkotlinbaseproject.network.exceptions

import com.android.volley.*
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Trace exceptions(api call or parse data or connection errors) &
 * depending on what exception returns [ApiError]
 *
 * */
fun traceErrorException(throwable: Throwable?): ApiError {

    return when (throwable) {

        is HttpException -> {
            when (throwable.code()) {
                400 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.BAD_REQUEST
                )
                401 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.UNAUTHORIZED
                )
                403 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.FORBIDDEN
                )
                404 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.NOT_FOUND
                )
                405 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.METHOD_NOT_ALLOWED
                )
                409 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.CONFLICT
                )
                500 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.INTERNAL_SERVER_ERROR
                )
                else -> ApiError(
                    UNKNOWN_ERROR_MESSAGE,
                    0,
                    ApiError.ErrorStatus.UNKNOWN_ERROR
                )
            }
        }

        is SocketTimeoutException -> {
            ApiError(throwable.message, 408, ApiError.ErrorStatus.TIMEOUT)
        }

        is IOException -> {
            ApiError(throwable.message, 499, ApiError.ErrorStatus.NO_CONNECTION)
        }

        else -> ApiError(UNKNOWN_ERROR_MESSAGE, 0, ApiError.ErrorStatus.UNKNOWN_ERROR)
    }
}

fun volleyTraceErrorException(error: VolleyError?): String{
    when (error) {
        is NetworkError -> {
            //handle your network error here.
            return "Network Error"
        }
        is ServerError -> {
            //handle if server error occurs with 5** status code
            return "Server Error"
        }
        is AuthFailureError -> {
            //handle if authFailure occurs.This is generally because of invalid credentials
            return "Authentication Failure Error"
        }
        is ParseError -> {
            //handle if the volley is unable to parse the response data.
            return "Parse Error"
        }
        is NoConnectionError -> {
            //handle if no connection is occurred
            return "No Connection"
        }
        is TimeoutError -> {
            //handle if socket time out is occurred.
            return "Time out"
        }
        else -> return "Unknown Error"
    }
}