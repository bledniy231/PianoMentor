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
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Arrays
import javax.inject.Singleton
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*@Provides
    @Singleton
    fun provideSslContext(@ApplicationContext context: Context): SSLContext {
        val clientCertInputStream = context.resources.openRawResource(R.raw.DiplomaClientCertificationKey);

        return try {
            // Создание объекта X509Certificate для клиентского сертификата
            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
            val clientCertificate: X509Certificate = certificateFactory.generateCertificate(clientCertInputStream) as X509Certificate

            // Создание объекта KeyStore и добавление клиентского сертификата
            val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("alias", clientCertificate)

            // Создание объекта TrustManagerFactory и инициализация с keyStore
            val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            // Создание объекта SSLContext и инициализация с trustManagerFactory
            val sslContext: SSLContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagerFactory.trustManagers, null)

            sslContext
        } catch (e: Exception) {
            // Обработка ошибок
            e.printStackTrace()
            // Возвращаем значение по умолчанию в случае ошибки
            SSLContext.getDefault()
        } finally {
            // Важно закрыть InputStream после использования
            clientCertInputStream.close()
        }
    }*/

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
//        val clientCertInputStream = context.resources.openRawResource(R.raw.diploma_client_certification_key);
//        var sslContext: SSLContext? = null
//        var trustManager: TrustManager? = null
//
//        try {
//            // Создание объекта X509Certificate для клиентского сертификата
//            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
//            //val clientCertificate: X509Certificate = certificateFactory.generateCertificate(clientCertInputStream) as X509Certificate
//
//            // Создание объекта KeyStore и добавление клиентского сертификата
//            val keyStore: KeyStore = KeyStore.getInstance("PKCS12"/*KeyStore.getDefaultType()*/)
//            keyStore.load(clientCertInputStream, "Borismybestfriend111".toCharArray())
//            //keyStore.setCertificateEntry("alias", clientCertificate)
//            val keyManagerFactory = KeyManagerFactory.getInstance("X509")
//            keyManagerFactory.init(keyStore, "Borismybestfriend111".toCharArray())
//
//            // Создание объекта TrustManagerFactory и инициализация с keyStore
//            val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//            trustManagerFactory.init(keyStore)
//            if (trustManagerFactory.trustManagers.size != 1 || (trustManagerFactory.trustManagers[0] !is X509TrustManager)) {
//                throw IllegalStateException("Unexpected default trust managers:" + trustManagerFactory.trustManagers.contentToString());
//            }
//            trustManager = trustManagerFactory.trustManagers[0]
//
//            // Создание объекта SSLContext и инициализация с trustManagerFactory
//            sslContext = SSLContext.getInstance("TLS")
//            sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, SecureRandom())
//        } catch (e: Exception) {
//            // Обработка ошибок
//            e.printStackTrace()
//            // Возвращаем значение по умолчанию в случае ошибки
//            sslContext = SSLContext.getDefault()
//        } finally {
//            // Важно закрыть InputStream после использования
//            clientCertInputStream.close()
//        }
//        return sslContext?.socketFactory?.let {
//            OkHttpClient.Builder()
//                .addInterceptor(authInterceptor)
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                .sslSocketFactory(it, trustManager as X509TrustManager)
//                .build()
//        }
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

//private const val BASE_URL = "https://192.168.0.105:8080/"      // Docker
//private const val BASE_URL = "https://192.168.0.105:8181/"    // Development
//private const val BASE_URL = "https://10.0.2.2:8080/"         // Localhost Docker
//private const val BASE_URL = "https://10.0.2.2:8181/"         // Localhost Development
//private const val BASE_URL = "https://192.168.0.3:8080/"
private const val BASE_URL = "https://192.168.134.173:8080/"