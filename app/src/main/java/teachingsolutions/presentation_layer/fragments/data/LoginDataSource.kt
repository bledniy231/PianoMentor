package teachingsolutions.presentation_layer.fragments.data

import teachingsolutions.presentation_layer.fragments.data.model.LoggedInUserModel
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUserModel> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUserModel(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}