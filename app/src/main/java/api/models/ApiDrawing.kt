package api.models

import com.squareup.moshi.Json

data class ApiDrawing (

    @Json(name = "frames")
    var frames: Frames? = null

)