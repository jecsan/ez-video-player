package api.models

import com.squareup.moshi.Json

data class Keyframe4 (

    @Json(name = "kickleg")
    var kickleg: Kickleg? = null,
    @Json(name = "direction")
    var direction: String? = null,
    @Json(name = "plantleg")
    var plantleg: Plantleg? = null,
    @Json(name = "time")
    var time: Double? = null

)