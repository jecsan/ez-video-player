package api.models

import com.squareup.moshi.Json

data class Kickleg (

    @Json(name = "ap")
    var ap: List<Double>? = null,
    @Json(name = "hp")
    var hp: List<Double>? = null,
    @Json(name = "kp")
    var kp: List<Double>? = null,
    @Json(name = "angles")
    var angles: Map<String,Double>? = null

)