package com.rorpheeyah.androidkotlinbaseproject.base

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

/*
     ____                 _____                                     _
    | __ )  __ _ ___  ___|  ___| __ __ _  __ _ _ __ ___   ___ _ __ | |_
    |  _ \ / _` / __|/ _ \ |_ | '__/ _` |/ _` | '_ ` _ \ / _ \ '_ \| __|
    | |_) | (_| \__ \  __/  _|| | | (_| | (_| | | | | | |  __/ | | | |_
    |____/ \__,_|___/\___|_|  |_|  \__,_|\__, |_| |_| |_|\___|_| |_|\__|
                                         |___/
 */
abstract class BaseFragment<V : BaseViewModel, B : ViewDataBinding> : Fragment(),
    BaseViewGroup<V, B>,
    ToolbarManager,
    ProgressBarManager {

    override lateinit var binding: B
    abstract var title: String
    abstract var menuId: Int
    var navigationListener: NavigationListener? = null
    var backCallback: MutableLiveData<OnBackPressedListener?>? = null
    override var toolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initializeNavigationListener()
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        if (menuId > 0) {
            setHasOptionsMenu(true)
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            if (toolBarId > 0) {
                toolbar = view.findViewById(toolBarId)
                (activity as AppCompatActivity).setSupportActionBar(toolbar)
                toolbar?.title = title
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toolbar?.visibility = View.GONE
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.commonMessage.observe(viewLifecycleOwner, {
            it?.let { message ->
                showMessage(message)
            }
        })
        viewModel.hideLoading()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (menuId != 0) {
            inflater.inflate(menuId, menu)
        }
        return super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        arguments?.putBoolean(DATA_LOADED, viewModel.dataLoaded.value == true)
    }

    override fun onDestroy() {
        if (backCallback != null && backCallback?.value != null) {
            backCallback?.value = null
        }
        super.onDestroy()
    }


    private fun initializeNavigationListener() {
        if (navigationListener != null)
            return

        if (activity is BaseActivity<*, *>) {
            navigationListener = activity as BaseActivity<*, *>
        }
    }

    private fun showMessage(message: String, duration: Int = Toast.LENGTH_LONG) {
        context?.let {
            Toast.makeText(it, message, duration).show()
        }
    }

    companion object {
        const val DATA_LOADED = "DATA-LOADED"
    }
}