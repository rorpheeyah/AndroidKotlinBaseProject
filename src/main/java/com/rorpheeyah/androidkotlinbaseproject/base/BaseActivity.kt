package com.rorpheeyah.androidkotlinbaseproject.base

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rorpheeyah.androidkotlinbaseproject.base.*
import com.rorpheeyah.androidkotlinbaseproject.biometric.BiometricAuthenticator
import com.rorpheeyah.androidkotlinbaseproject.network.connection.NetworkCallback
import com.rorpheeyah.androidkotlinbaseproject.util.dialog.LoadingDialog
import com.rorpheeyah.androidkotlinbaseproject.util.extensions.TransitionAnimation
import com.rorpheeyah.androidkotlinbaseproject.util.extensions.setCustomAnimation
import java.util.*
import kotlin.concurrent.schedule


/*
     ____                    _        _   _       _ _
    | __ )  __ _ ___  ___   / \   ___| |_(_)_   _(_) |_ _   _
    |  _ \ / _` / __|/ _ \ / _ \ / __| __| \ \ / / | __| | | |
    | |_) | (_| \__ \  __// ___ \ (__| |_| |\ V /| | |_| |_| |
    |____/ \__,_|___/\___/_/   \_\___|\__|_| \_/ |_|\__|\__, |
                                                        |___/
 */
/**
 * @author Matt Dev
 * @since 2021.02.05
 */
abstract class BaseActivity<V : BaseViewModel, B : ViewDataBinding>: AppCompatActivity(),
    BaseViewGroup<V, B>, NavigationListener {

    private lateinit var networkCallback: NetworkCallback
    private lateinit var biometricAuthenticator: BiometricAuthenticator

    final override lateinit var binding: B
    abstract var frameContainerId: Int
    private val backCallback: MutableLiveData<OnBackPressedListener?> = MutableLiveData()
    private var lastFragmentTag = ""

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        try {
            // observe network connection
            networkCallback = NetworkCallback(this)
            networkCallback.observeNetworkConnection(object : NetworkCallback.NetworkObservation{
                override fun isActive(isOnline: Boolean) {
                    Toast.makeText(this@BaseActivity, if(isOnline) "Back to Connection" else "No internet", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // biometric
        biometricAuthenticator =
            BiometricAuthenticator.instance(this, object : BiometricAuthenticator.Listener {
                override fun onNewMessage(message: String) {
                    // TODO: Log your message
                }
            })

        biometricAuthenticator.biometricListener = object : BiometricAuthenticator.BiometricListener{
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Toast.makeText(this@BaseActivity, "SUCCEED", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(this@BaseActivity, "ERROR $errString", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@BaseActivity, "FAILED", Toast.LENGTH_LONG).show()
            }
        }

        // show negative/cancel
        biometricAuthenticator.showNegativeButton = true

        // loading observe
        viewModel.loadingMLD.observe(this, Observer {
            if(it){
                showLoading()
            }else {
                hideLoading()
            }
        })
    }

    override fun onBackPressed() {
        if (backCallback.value == null) {
            if (!canBack())
                super.onBackPressed()
        } else {
            if (backCallback.value?.onBackPressed(this) == false){
                if (!canBack()) {
                    super.onBackPressed()
                }
            }
        }
    }

    private fun canBack(): Boolean {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
            return true
        }

        supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
            .name?.let {
                supportFragmentManager.findFragmentByTag(it)?.apply {
                    if (childFragmentManager.backStackEntryCount != 0) {
                        childFragmentManager.popBackStack()
                        return true
                    }

                }
            }
        return false
    }

    override fun onResume() {
        super.onResume()
        // register network callback
        networkCallback.registerNetworkCallback()
    }

    override fun onPause() {
        // register network callback
        networkCallback.registerNetworkCallback()
        super.onPause()
    }

    override fun onDestroy() {
        // unregister network callback
        networkCallback.unregisterNetworkCallback()

        if (backCallback.value != null) {
            backCallback.value = null
        }
        super.onDestroy()
    }

    override fun navigateTo(fragment: Fragment, backStackTag: String, animationType: TransitionAnimation, isAdd: Boolean) {
        if (lastFragmentTag.equals(backStackTag, ignoreCase = true)) {
            Log.e("BaseActivity", "Cannot navigate to the current fragment. It's already visible on the screen")
            return
        }

        if (frameContainerId == 0) {
            Log.e("BaseActivity", "No container is defined to navigate on!")
            return
        }

        if (supportFragmentManager.backStackEntryCount > 0 && isAdd) {
            val tag = supportFragmentManager
                .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                .name

            supportFragmentManager.findFragmentByTag(tag)?.apply {
                this.onPause()
            }
        }

        supportFragmentManager.findFragmentByTag(backStackTag)?.let {
            supportFragmentManager.popBackStack(backStackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            return
        }


        supportFragmentManager.beginTransaction().apply {
            setCustomAnimation(animationType)
            if (isAdd) {
                add(frameContainerId, fragment, backStackTag)
            } else {
                replace(frameContainerId, fragment, backStackTag)
            }
            //addToBackStack(backStackTag)
            commitAllowingStateLoss()
        }

        lastFragmentTag = backStackTag
    }

    private fun showLoading() {
        loadingDialog = LoadingDialog(this, viewModel.scope)
        loadingDialog!!.show()
    }

    private fun hideLoading() {
        if(loadingDialog != null){
            Timer().schedule(1000){
                loadingDialog!!.hide()
            }
        }
    }

    /**
     * Show System dialog credentials
     */
    fun showCredentialScreen(){
        biometricAuthenticator.authenticateWithoutCrypto(this)
    }

}