package api.models

import com.squareup.moshi.Json

data class Texttips (

    @Json(name = "name")
    var name: String? = null,
    @Json(name = "key")
    var key: String? = null,
    @Json(name = "value")
    var value: String? = null
)