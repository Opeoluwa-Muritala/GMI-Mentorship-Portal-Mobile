package org.globalmentorship.portal

import kotlinx.coroutines.test.runTest
import org.globalmentorship.portal.data.repository.FakeMessageRepository
import org.globalmentorship.portal.domain.models.MessageTab
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessageRepositoryTest {

    @Test
    fun testInitialRecipientsFixedList() = runTest {
        val repo = FakeMessageRepository()
        val recipients = repo.availableRecipients.value

        assertEquals(2, recipients.size)
        assertTrue(recipients.any { it.isGmiSupport })
        assertTrue(recipients.any { it.displayName == "Sarah Jenkins" })
    }

    @Test
    fun testCreateConversationAndVerifyTabs() = runTest {
        val repo = FakeMessageRepository()
        val recipient = repo.availableRecipients.value.first()

        val result = repo.createThread(
            recipientId = recipient.id,
            subject = "Resume Feedback Request",
            contentHtml = "<p>Hi, can you review my updated resume draft?</p>"
        )

        assertTrue(result.isSuccess)
        val createdThread = result.getOrNull()
        assertEquals("Resume Feedback Request", createdThread?.subject)
        assertEquals(MessageTab.INBOX, createdThread?.tab)

        // Archive thread and verify separation
        createdThread?.id?.let { threadId ->
            repo.archiveThread(threadId)
            assertEquals(0, repo.inboxThreads.value.count { it.id == threadId })
            assertEquals(1, repo.archivedThreads.value.count { it.id == threadId })
        }
    }

    @Test
    fun testSendMessageAppendsChronologically() = runTest {
        val repo = FakeMessageRepository()
        val initialThread = repo.inboxThreads.value.first()
        val initialMsgCount = initialThread.messages.size

        val sendResult = repo.sendMessage(
            threadId = initialThread.id,
            contentHtml = "<p>Confirmed for our session!</p>"
        )
        assertTrue(sendResult.isSuccess)

        val updatedMessages = repo.getThreadMessages(initialThread.id)
        assertEquals(initialMsgCount + 1, updatedMessages.size)
        assertEquals("<p>Confirmed for our session!</p>", updatedMessages.last().contentHtml)
    }
}
