package teachingsolutions.data_access_layer.shared_preferences_keys

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesKeys @Inject constructor() {
    public val PREF_NAME = "teaching_solutions"
    public val KEY_USER_TOKENS = "user_tokens"
    public val KEY_AUTH_TOKEN = "auth_token"
    public val KEY_AUTH_TOKEN_EXPIRY_TIME = "auth_token_expiry_time"
    public val KEY_REFRESH_TOKEN = "refresh_token"
    public val KEY_REFRESH_TOKEN_EXPIRY_TIME = "refresh_token_expiry_time"
    public val KEY_USERID = "user_id"
    public val KEY_USERNAME = "username"
    public val KEY_EMAIL = "email"
    public val KEY_ROLES = "roles"
}