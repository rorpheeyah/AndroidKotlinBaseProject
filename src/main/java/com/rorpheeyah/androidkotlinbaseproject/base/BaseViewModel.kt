package com.rorpheeyah.androidkotlinbaseproject.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/*
     ____               __     ___               __  __           _      _
    | __ )  __ _ ___  __\ \   / (_) _____      _|  \/  | ___   __| | ___| |
    |  _ \ / _` / __|/ _ \ \ / /| |/ _ \ \ /\ / / |\/| |/ _ \ / _` |/ _ \ |
    | |_) | (_| \__ \  __/\ V / | |  __/\ V  V /| |  | | (_) | (_| |  __/ |
    |____/ \__,_|___/\___| \_/  |_|\___| \_/\_/ |_|  |_|\___/ \__,_|\___|_|

 */
/**
 * @author Matt Dev
 * @since 2021.02.05
 */
open class BaseViewModel() : ViewModel(){
    val loadingMLD: MutableLiveData<Boolean> = MutableLiveData()
    val commonMessage: MutableLiveData<String> = MutableLiveData()
    val dataLoaded: MutableLiveData<Boolean> = MutableLiveData()

    val scope = CoroutineScope(
        Job() + Dispatchers.Main
    )

    // Cancel the job when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    init {
        loadingMLD.value = false
    }

    open fun showLoading() {
        loadingMLD.value = true
    }

    open fun hideLoading() {
        loadingMLD.value = false
    }
}