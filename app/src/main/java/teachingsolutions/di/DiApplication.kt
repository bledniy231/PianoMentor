package teachingsolutions.di

import android.app.Application
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import teachingsolutions.data_access_layer.DAL_models.user.JwtTokens
import teachingsolutions.data_access_layer.DAL_models.user.LoginUserResponse
import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.shared_preferences_keys.SharedPreferencesKeys
import teachingsolutions.domain_layer.common.CustomGson
import teachingsolutions.domain_layer.login_registration.UserRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltAndroidApp
class DiApplication: Application() {
    override fun onCreate() {
        super.onCreate()
//        CoroutineScope(Dispatchers.Main).launch {
//            checkTokenExpiration()
//        }
    }

//    @Inject lateinit var sharedPreferences: SharedPreferences
//    @Inject lateinit var prefKeys: SharedPreferencesKeys
//    @Inject lateinit var customGson: CustomGson
//    @Inject lateinit var userRepository: UserRepository
//
//    private suspend fun checkTokenExpiration(): Boolean {
//        val gson = customGson.getCustomGsonObject()
//        val jwtTokens = sharedPreferences.getString(prefKeys.KEY_USER_TOKENS, null)?.let { gson.fromJson(it, JwtTokens::class.java) }
//        val userId = sharedPreferences.getString(prefKeys.KEY_USERID, null)?.toLong()
//        val userName = sharedPreferences.getString(prefKeys.KEY_USERNAME, null)
//        val email = sharedPreferences.getString(prefKeys.KEY_EMAIL, null)
//        val roles = sharedPreferences.getStringSet(prefKeys.KEY_ROLES, null)?.toList()
//
//        if (jwtTokens != null && userId != null && userName != null && email != null && roles != null)
//        {
//            if (!isTokenExpired(jwtTokens.accessTokenExpireTime) && !isTokenExpired(jwtTokens.refreshTokenExpireTime))
//            {
//                userRepository.setLoggedInUser(LoginUserResponse(userId, userName, email, jwtTokens, roles))
//                return true
//            }
//            else if (!isTokenExpired(jwtTokens.refreshTokenExpireTime))
//            {
//                val result = userRepository.refreshUserTokens(jwtTokens)
//                if (result is ActionResult.Success)
//                {
//                    result.data.jwtTokens?.let { newTokens ->
//                        userRepository.setLoggedInUser(LoginUserResponse(userId, userName, email, newTokens, roles))
//                    }
//                    return true
//                }
//                userRepository.logout()
//                return false
//            }
//            else
//            {
//                userRepository.logout()
//                return false
//            }
//        }
//        else
//        {
//            return false
//        }
//    }
//
//    private fun isTokenExpired(tokenExpireTime: LocalDateTime?): Boolean {
//        return tokenExpireTime?.isAfter(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)) == true
//    }
}