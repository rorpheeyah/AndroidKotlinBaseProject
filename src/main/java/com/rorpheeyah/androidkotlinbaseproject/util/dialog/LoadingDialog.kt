package com.rorpheeyah.androidkotlinbaseproject.util.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.rorpheeyah.androidkotlinbaseproject.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoadingDialog(private val mContext: Context, private val scope: CoroutineScope) {
    private var progress: Dialog? = null

    @SuppressLint("InflateParams")
    fun show() {
        scope.launch {
            if (progress == null) {
                val loadingView = LayoutInflater.from(mContext).inflate(R.layout.layout_loading, null)
                Glide.with(mContext).load(R.drawable.loading).into(loadingView.findViewById(R.id.iv_loading))

                progress = Dialog(mContext)
                progress!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    progress!!.window?.insetsController?.hide(WindowInsets.Type.statusBars())
                } else {
                    progress!!.window?.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                }
                progress!!.setContentView(loadingView)
                progress!!.setCanceledOnTouchOutside(false)
            }
            progress!!.show()
        }
    }

    fun hide(){
        if (progress == null) return
        if(progress!!.isShowing) progress!!.dismiss()
    }
}