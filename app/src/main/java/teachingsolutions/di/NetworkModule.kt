package teachingsolutions.di

import android.content.Context
import com.example.pianomentor.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import teachingsolutions.data_access_layer.api.AuthInterceptor
import teachingsolutions.data_access_layer.api.IPianoMentorApiService
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, @ApplicationContext context: Context): OkHttpClient? {
        val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient?): Retrofit {
        if (okHttpClient == null) {
            throw IllegalArgumentException("OkHttpClient is null")
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): LocalDateTime {
                    val dateTimeString = json.asString
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                    return LocalDateTime.parse(dateTimeString, formatter)
                }
            })
            .registerTypeAdapter(LocalDateTime::class.java, object : JsonSerializer<LocalDateTime> {
                override fun serialize(
                    src: LocalDateTime?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                    return JsonPrimitive(src?.format(formatter))
                }
            })
            .create()

        return Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideIPianoMentorApiService(retrofit: Retrofit): IPianoMentorApiService {
        return retrofit.create(IPianoMentorApiService::class.java)
    }
}

private const val BASE_URL = "https://192.168.0.105:8080/"      // Docker
//private const val BASE_URL = "https://192.168.0.105:8181/"    // Development
//private const val BASE_URL = "https://10.0.2.2:8080/"         // Localhost Docker
//private const val BASE_URL = "https://10.0.2.2:8181/"         // Localhost Development
//private const val BASE_URL = "https://192.168.0.3:8080/"
//private const val BASE_URL = "https://192.168.134.173:8080/"