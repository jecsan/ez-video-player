package api.models

import com.squareup.moshi.Json

data class Plantleg (

    @Json(name = "angles")
    var angles: Map<String,Double>? = null

)