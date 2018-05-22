package api

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import viewmodel.VideoViewModel

class MyViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoViewModel((application as VideoPlayerApp).sparrowApi ) as T
    }
}