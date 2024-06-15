package teachingsolutions.presentation_layer.fragments.common

import java.io.File

data class FileResultUI(
    val success: File? = null,
    val error: String? = null
)