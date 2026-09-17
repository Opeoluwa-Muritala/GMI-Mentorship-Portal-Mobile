package org.globalmentorship.portal

import kotlinx.coroutines.test.runTest
import org.globalmentorship.portal.data.repository.FakeCalendarRepository
import org.globalmentorship.portal.domain.models.MeetingStatus
import org.globalmentorship.portal.domain.models.SlotAvailabilityType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarRepositoryTest {

    @Test
    fun testTimezoneConfigurationEnforcement() = runTest {
        val repo = FakeCalendarRepository()
        assertTrue(repo.isTimezoneConfigured.value)

        repo.setTimezone("")
        assertEquals(false, repo.isTimezoneConfigured.value)

        repo.setTimezone("America/New_York")
        assertEquals(true, repo.isTimezoneConfigured.value)
        assertEquals("America/New_York", repo.selectedTimezone.value)
    }

    @Test
    fun testAddAvailabilitySlotAndVerifyType() = runTest {
        val repo = FakeCalendarRepository()
        val initialCount = repo.currentMonthSlots.value.size

        repo.addAvailabilitySlot("2026-09-20", 14, 15)
        val updatedSlots = repo.currentMonthSlots.value

        assertEquals(initialCount + 1, updatedSlots.size)
        val added = updatedSlots.find { it.dateIso == "2026-09-20" }
        assertEquals(SlotAvailabilityType.STUDENT_AVAILABLE, added?.type)
        assertTrue(added?.isUserSlot == true)
    }

    @Test
    fun testConfirmProposedMeeting() = runTest {
        val repo = FakeCalendarRepository()
        val pendingMeeting = repo.suggestedMeetings.value.first()
        assertEquals(MeetingStatus.PENDING, pendingMeeting.status)

        val confirmResult = repo.confirmMeeting(pendingMeeting.id)
        assertTrue(confirmResult.isSuccess)

        val updated = repo.suggestedMeetings.value.first()
        assertEquals(MeetingStatus.CONFIRMED, updated.status)
    }
}
