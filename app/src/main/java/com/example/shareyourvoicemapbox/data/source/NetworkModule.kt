package com.example.shareyourvoicemapbox.data.source

import android.util.Log
import com.example.shareyourvoicemapbox.data.constants.Constants.HOST
import com.example.shareyourvoicemapbox.data.dto.AuthResponseDTO
import com.example.shareyourvoicemapbox.data.dto.RefreshTokenDTO
import com.example.shareyourvoicemapbox.data.source.auth.storage.TokenStorage
import com.example.shareyourvoicemapbox.domain.auth.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        tokenStorage: TokenStorage,
        sessionManager: SessionManager,
    ): HttpClient {

        return HttpClient(CIO) {

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }

            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Log.d("KTOR", message)
                    }
                }
            }
            install(plugin = Auth) {
                bearer {

                    loadTokens {
                        BearerTokens(
                            tokenStorage.getAccessToken() ?: "",
                            tokenStorage.getRefreshToken() ?: "",
                        )
                    }

                    refreshTokens {

                        Log.d("REFRESH", "TRIGGERED")

                        val refreshToken = tokenStorage.getRefreshToken()

                        if (refreshToken == null) {
                            tokenStorage.clear()
                            sessionManager.logout()
                            Log.d("SESSION", "NO REFRESH TOKEN → LOGOUT")
                            return@refreshTokens null
                        }

                        val response = client.post("$HOST/api/auth/refresh") {
                            contentType(ContentType.Application.Json)
                            setBody(
                                RefreshTokenDTO(
                                    refreshToken,
                                ),
                            )
                        }

                        if (response.status != HttpStatusCode.OK) {
                            tokenStorage.clear()
                            sessionManager.logout()
                            Log.d("REFRESH", response.status.toString())
                            return@refreshTokens null
                        }

                        val body = response.body<AuthResponseDTO>()

                        tokenStorage.save(
                            body.accessToken,
                            body.refreshToken,
                        )

                        BearerTokens(
                            body.accessToken,
                            body.refreshToken,
                        )
                    }
                }
            }
        }
    }
}