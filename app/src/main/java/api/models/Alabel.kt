package api.models

import com.squareup.moshi.Json

data class Alabel (

    @Json(name = "keyframe")
    var keyframe: String? = null,
    @Json(name = "key")
    var key: String? = null,
    @Json(name = "label")
    var label: String? = null

)