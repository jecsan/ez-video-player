package api.models

import com.squareup.moshi.Json

data class ApiDrawing (

    @Json(name = "frames")
    var frames: Frames? = null,
    @Json(name = "assessments")
    var assessments: List<Assessments>? = null,
    @Json(name = "textips")
    var textips: List<Texttips>? = null

)