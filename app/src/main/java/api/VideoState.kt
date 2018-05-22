package api

import okhttp3.ResponseBody

sealed class VideoState {
    abstract val data: ResponseBody?
}

data class DefaultState(override val data: ResponseBody?) : VideoState()
data class LoadingState(override val data: ResponseBody?) : VideoState()
data class ErrorState(val error: Throwable, override val data: ResponseBody?) : VideoState()