package api.models

import com.squareup.moshi.Json

data class Keyframe3(

    @Json(name = "body")
    var body: Body? = null,
    @Json(name = "kickleg")
    var kickleg: Kickleg? = null,
    @Json(name = "direction")
    var direction: String? = null,
    @Json(name = "plantleg")
    var plantleg: Plantleg? = null,
    @Json(name = "time")
    var time: Double? = null,
    @Json(name = "ball")
    var ball: List<Int>? = null
)