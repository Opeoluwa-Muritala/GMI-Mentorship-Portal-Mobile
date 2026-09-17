package org.globalmentorship.portal

import kotlinx.coroutines.test.runTest
import org.globalmentorship.portal.data.repository.FakeProgramRepository
import org.globalmentorship.portal.domain.models.SessionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProgramRepositoryTest {

    @Test
    fun testInitialSessionsStateAndSequentialOrder() = runTest {
        val repo = FakeProgramRepository()
        val sessions = repo.sessions.value
        assertEquals(10, sessions.size)

        // Verify seeded real titles
        assertEquals("Welcome to Your Mentorship", sessions[0].title)
        assertEquals("Create a Resume/CV", sessions[1].title)
        assertEquals("Build a Professional Network", sessions[2].title)
        assertEquals("Develop a Career Plan", sessions[3].title)
        assertEquals("Develop Professional Skills Using SMART Goals", sessions[4].title)
        assertEquals("Prepare for a Job Interview", sessions[5].title)
        assertEquals("Prepare for a Job Interview", sessions[6].title)
        assertEquals("Understand Job Search Techniques", sessions[7].title)
        assertEquals("Distinguish Yourself in the Workplace", sessions[8].title)
        assertEquals("Prepare for Global Business and Conclude Mentorship", sessions[9].title)

        // Check sequential unlock initial states
        assertEquals(SessionStatus.COMPLETED, sessions[0].status)
        assertEquals(SessionStatus.CURRENT, sessions[6].status) // Session 7 is current
        assertEquals(SessionStatus.LOCKED, sessions[7].status)  // Session 8 is locked
    }

    @Test
    fun testCannotCompleteLockedSession() = runTest {
        val repo = FakeProgramRepository()
        val result = repo.markSessionComplete(8) // Session 8 is locked initially
        assertTrue(result.isFailure)
    }

    @Test
    fun testCompletingCurrentSessionUnlocksNextAndNotifiesPartner() = runTest {
        val repo = FakeProgramRepository()
        val result = repo.markSessionComplete(7)
        assertTrue(result.isSuccess)

        val updatedSession7 = result.getOrNull()
        assertEquals(SessionStatus.COMPLETED, updatedSession7?.status)
        assertTrue(updatedSession7?.partnerNotified == true)

        // Session 8 should now be unlocked and CURRENT
        val updatedSessions = repo.sessions.value
        assertEquals(SessionStatus.CURRENT, updatedSessions[7].status)
        assertEquals(7, repo.programOverview.value.completedSessions)
    }
}
