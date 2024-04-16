package teachingsolutions.presentation_layer.fragments.data.login_registration.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUserModel(
    val userId: String,
    val displayName: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val userRoles: List<String>
)