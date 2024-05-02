package teachingsolutions.presentation_layer.fragments.lecture.model

import java.io.File

data class LecturePdfResultUI(
    val success: File? = null,
    val error: String? = null
)