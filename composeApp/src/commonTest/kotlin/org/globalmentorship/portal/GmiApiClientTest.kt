package org.globalmentorship.portal

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.globalmentorship.portal.data.remote.GmiApiClientImpl
import org.globalmentorship.portal.data.remote.LoginRequest
import org.globalmentorship.portal.domain.models.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals

class GmiApiClientTest {

    @Test
    fun testLoginSuccessMock() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                    {
                        "token": "mock_jwt_token_header.body.sig",
                        "refreshToken": "mock_refresh_token",
                        "user": {
                            "id": "u1",
                            "firstName": "Alex",
                            "lastName": "Mwangi",
                            "email": "alex.mwangi@student.uonbi.ac.ke",
                            "role": "STUDENT",
                            "createdAt": "2025-01-10",
                            "updatedAt": "2025-02-14",
                            "biometricEnabled": true
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }

        val apiClient = GmiApiClientImpl(client)
        val response = apiClient.login(LoginRequest("alex@student.uonbi.ac.ke", "secret123"))

        assertEquals("mock_jwt_token_header.body.sig", response.token)
        assertEquals("Alex", response.user.firstName)
        assertEquals(UserRole.STUDENT, response.user.role)
    }

    @Test
    fun testGetProgramOverviewMock() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                    {
                        "programName": "GMI Mentorship",
                        "completedSessions": 6,
                        "totalSessions": 10,
                        "currentSessionNumber": 7
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }

        val apiClient = GmiApiClientImpl(client)
        val overview = apiClient.getProgramOverview()

        assertEquals(6, overview.completedSessions)
        assertEquals(10, overview.totalSessions)
        assertEquals(60, overview.progressPercentage)
    }
}
