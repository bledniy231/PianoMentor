package teachingsolutions.presentation_layer.fragments.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUserModel(
    val userId: String,
    val displayName: String
)