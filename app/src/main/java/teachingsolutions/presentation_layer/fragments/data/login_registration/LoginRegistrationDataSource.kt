package teachingsolutions.presentation_layer.fragments.data.login_registration

import teachingsolutions.presentation_layer.fragments.data.common.ActionResult
import teachingsolutions.presentation_layer.fragments.data.login_registration.model.LoggedInUserModel
import java.io.IOException
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginRegistrationDataSource {

    fun login(username: String, password: String): ActionResult<LoggedInUserModel> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUserModel(UUID.randomUUID().toString(), "Jane Doe", "1", "1", "1", listOf("admin"))
            return ActionResult.Success(fakeUser)
        } catch (e: Throwable) {
            return ActionResult.Error(IOException("Error logging in", e))
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String): ActionResult<LoggedInUserModel> {
        try {

            val fakeUser = LoggedInUserModel(UUID.randomUUID().toString(), "Jane Doe", "1", "1", "1", listOf("admin"))
            return ActionResult.Success(fakeUser)
        } catch (e: Throwable) {
            return ActionResult.Error(IOException("Error register in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}