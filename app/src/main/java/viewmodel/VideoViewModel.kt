package viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import api.*
import api.models.ApiDrawing

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoViewModel(private val sparrowApi: SparrowApi) : ViewModel() {

    val mutableLiveData = MutableLiveData<VideoState>()

    init {
        mutableLiveData.value = DefaultState(null)
    }


    fun loadVideoData(){
        mutableLiveData.value = LoadingState(null)
        sparrowApi.frames.enqueue(object : Callback<ApiDrawing>{
            override fun onFailure(call: Call<ApiDrawing>?, t: Throwable) {
                t.printStackTrace()
                mutableLiveData.value = ErrorState(t,null)
            }

            override fun onResponse(call: Call<ApiDrawing>, response: Response<ApiDrawing>) {
                mutableLiveData.value = DefaultState(response.body())
            }

        })
    }

}
