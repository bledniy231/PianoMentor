package teachingsolutions.data_access_layer.api

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import teachingsolutions.data_access_layer.common.SharedPreferencesKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(private val sharedPreferences: SharedPreferences): Interceptor {
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

    private fun getTokenFromStorage(): String {
        return sharedPreferences.getString(SharedPreferencesKeys.AUTH_TOKEN, "") ?: ""
    }
}