package org.globalmentorship.portal

import kotlinx.coroutines.test.runTest
import org.globalmentorship.portal.data.repository.FakeAuthRepository
import org.globalmentorship.portal.domain.models.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryTest {

    @Test
    fun testAuthenticationAndRoleAssignment() = runTest {
        val repo = FakeAuthRepository()
        assertTrue(repo.isAuthenticated.value)
        assertEquals(UserRole.STUDENT, repo.currentUser.value?.role)

        // Switch role demo check
        repo.switchDemoRole()
        assertEquals(UserRole.MENTOR, repo.currentUser.value?.role)

        // Logout
        repo.logout()
        assertEquals(false, repo.isAuthenticated.value)

        // Log back in with mentor email
        val loginResult = repo.login("mentor@gmi.org", "password123", true)
        assertTrue(loginResult.isSuccess)
        assertEquals(UserRole.MENTOR, repo.currentUser.value?.role)
    }

    @Test
    fun testCaptchaVerification() = runTest {
        val repo = FakeAuthRepository()
        assertTrue(repo.verifyCaptcha("sample_token_xyz"))
        assertEquals(false, repo.verifyCaptcha(""))
    }
}
