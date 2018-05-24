package api.models

import com.squareup.moshi.Json

data class Frames (

    @Json(name = "keyframe4")
    var keyframe4: Keyframe4? = null,
    @Json(name = "keyframe3")
    var keyframe3: Keyframe3? = null,
    @Json(name = "keyframe2")
    var keyframe2: Keyframe2? = null

)