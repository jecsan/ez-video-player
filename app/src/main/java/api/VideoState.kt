package api

import api.models.ApiDrawing
import okhttp3.ResponseBody

sealed class VideoState {
    abstract val data: ApiDrawing?
}

data class DefaultState(override val data: ApiDrawing?) : VideoState()
data class LoadingState(override val data: ApiDrawing?) : VideoState()
data class ErrorState(val error: Throwable, override val data: ApiDrawing?) : VideoState()