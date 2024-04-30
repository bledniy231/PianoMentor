package teachingsolutions.data_access_layer.api

import android.content.SharedPreferences
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import teachingsolutions.data_access_layer.DAL_models.user.JwtTokens
import teachingsolutions.data_access_layer.shared_preferences_keys.SharedPreferencesKeys
import teachingsolutions.domain_layer.common.CustomGson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val prefKeys: SharedPreferencesKeys,
    private val customGson: CustomGson
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url

        if (ApiEndpointsWithoutAuth.entries.any { it.value.equals(requestUrl) }) {
            val requestWithAuth = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + getTokenFromStorage())
                .build()

            return chain.proceed(requestWithAuth)
        }

        return chain.proceed(originalRequest)
    }

    private val gson = customGson.getCustomGsonObject()
    private fun getTokenFromStorage(): String {
        val jwtTokens = sharedPreferences.getString(prefKeys.KEY_USER_TOKENS, null)?.let { gson.fromJson(it, JwtTokens::class.java) }
        return jwtTokens?.accessToken ?: ""
    }
}