package teachingsolutions.presentation_layer.fragments.data.login_registration

import teachingsolutions.presentation_layer.fragments.data.common.ActionResult
import teachingsolutions.presentation_layer.fragments.data.login_registration.model.LoggedInUserModel

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRegistrationRepository(val dataSource: LoginRegistrationDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUserModel? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): ActionResult<LoggedInUserModel> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    fun register(username: String, email: String, password: String, confirmPassword: String): ActionResult<LoggedInUserModel> {
        // handle register
        val result = dataSource.register(username, email, password, confirmPassword)

        if (result is ActionResult.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUserModel: LoggedInUserModel) {
        this.user = loggedInUserModel
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}