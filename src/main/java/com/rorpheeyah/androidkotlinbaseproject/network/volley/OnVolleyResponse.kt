package com.rorp.rorpdevlibs.network.volley

import com.android.volley.VolleyError

interface OnVolleyResponse {
    fun onSuccess(response: String)

    fun onError(error: VolleyError?)
}