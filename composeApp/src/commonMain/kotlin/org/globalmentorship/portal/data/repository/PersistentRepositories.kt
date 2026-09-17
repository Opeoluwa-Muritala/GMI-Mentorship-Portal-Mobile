package org.globalmentorship.portal.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.globalmentorship.portal.data.local.GmiLocalDatabase
import org.globalmentorship.portal.data.remote.GmiApiClient
import org.globalmentorship.portal.domain.models.*

class PersistentAuthRepository(
    private val localDb: GmiLocalDatabase
) : AuthRepository {

    private val defaultStudent = UserProfile(
        id = "user_student_01",
        firstName = "Alex",
        lastName = "Mwangi",
        email = "alex.mwangi@student.uonbi.ac.ke",
        role = UserRole.STUDENT,
        avatarUrl = null,
        timezone = "Africa/Nairobi",
        createdAt = "2025-01-10",
        updatedAt = "2025-02-14",
        partner = PartnerInfo(
            id = "user_mentor_01",
            name = "Sarah Jenkins",
            titleOrMajor = "Senior Director of Engineering",
            organizationOrUniversity = "Google",
            email = "sarah.jenkins@google.com",
            role = UserRole.MENTOR
        ),
        biometricEnabled = true
    )

    private val defaultMentor = UserProfile(
        id = "user_mentor_01",
        firstName = "Sarah",
        lastName = "Jenkins",
        email = "sarah.jenkins@google.com",
        role = UserRole.MENTOR,
        avatarUrl = null,
        timezone = "America/New_York",
        createdAt = "2024-09-01",
        updatedAt = "2025-02-14",
        partner = PartnerInfo(
            id = "user_student_01",
            name = "Alex Mwangi",
            titleOrMajor = "Computer Science Senior",
            organizationOrUniversity = "University of Nairobi",
            email = "alex.mwangi@student.uonbi.ac.ke",
            role = UserRole.STUDENT
        ),
        biometricEnabled = true
    )

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(true)
    override val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    init {
        // Load from local on-phone database or initialize with seed
        val saved = localDb.getUserProfile()
        val user = saved ?: defaultStudent
        _currentUser.value = user
        if (saved == null) localDb.saveUserProfile(user)
    }

    override suspend fun login(email: String, password: String, rememberMe: Boolean): Result<UserProfile> {
        return if (email.isNotBlank() && password.length >= 6) {
            val user = if (email.contains("mentor", ignoreCase = true)) defaultMentor else defaultStudent
            _currentUser.value = user
            _isAuthenticated.value = true
            localDb.saveUserProfile(user)
            localDb.authToken = "jwt_token_sample_${System.currentTimeMillis()}"
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password."))
        }
    }

    override suspend fun loginWithBiometrics(): Result<UserProfile> {
        val user = _currentUser.value ?: defaultStudent
        _currentUser.value = user
        _isAuthenticated.value = true
        localDb.saveUserProfile(user)
        return Result.success(user)
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> = Result.success(Unit)

    override suspend fun verifyCaptcha(token: String): Boolean = token.isNotBlank()

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
        _currentUser.value = _currentUser.value?.copy(biometricEnabled = enabled)
        _currentUser.value?.let { localDb.saveUserProfile(it) }
    }

    override suspend fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
        localDb.authToken = null
    }

    override suspend fun switchDemoRole() {
        val next = if (_currentUser.value?.role == UserRole.STUDENT) defaultMentor else defaultStudent
        _currentUser.value = next
        localDb.saveUserProfile(next)
    }
}

class PersistentProgramRepository(
    private val localDb: GmiLocalDatabase,
    private val apiClient: GmiApiClient? = null
) : ProgramRepository {

    private val initialSessions = listOf(
        MentorshipSession(
            sessionNumber = 1,
            title = "Welcome to Your Mentorship",
            status = SessionStatus.COMPLETED,
            objectivesExcerpt = "Establish rapport, review program expectations, clarify roles, and set communication norms.",
            fullObjectives = listOf(
                "Introduce yourself and learn about your partner's background and aspirations.",
                "Review the 10-session roadmap and mutual expectations.",
                "Agree on meeting frequency, scheduling rules, and preferred communication tools.",
                "Commit to maintaining honest feedback and session completion integrity."
            ),
            materials = listOf(
                SessionMaterial("m1", "Session 1 Guide & Icebreakers", "PDF", "https://gmiportal.org/materials/session1.pdf", "1.2 MB", true)
            )
        ),
        MentorshipSession(
            sessionNumber = 2,
            title = "Create a Resume/CV",
            status = SessionStatus.COMPLETED,
            objectivesExcerpt = "Craft a competitive resume highlighting technical skills, education, and impact.",
            fullObjectives = listOf(
                "Review standard international CV/resume formatting rules.",
                "Transform job duties into outcome-oriented bullet points using action verbs.",
                "Incorporate targeted keywords matching applicant tracking systems (ATS).",
                "Receive mentor feedback on drafting and revision."
            ),
            materials = listOf(
                SessionMaterial("m2", "GMI Global Resume Template", "DOCX", "https://gmiportal.org/materials/session2_resume.docx", "850 KB", true)
            )
        ),
        MentorshipSession(
            sessionNumber = 3,
            title = "Build a Professional Network",
            status = SessionStatus.COMPLETED,
            objectivesExcerpt = "Optimize LinkedIn profile, expand strategic connections, and practice outreach.",
            fullObjectives = listOf(
                "Audit your LinkedIn profile for headshot, headline, and about section.",
                "Identify 5 key target companies and relevant professionals.",
                "Draft personalized connection request notes and follow-up templates.",
                "Understand professional etiquette for coffee chats and informational interviews."
            ),
            materials = listOf(
                SessionMaterial("m3", "LinkedIn Optimization Checklist", "PDF", "https://gmiportal.org/materials/session3_linkedin.pdf", "650 KB", true)
            )
        ),
        MentorshipSession(
            sessionNumber = 4,
            title = "Develop a Career Plan",
            status = SessionStatus.COMPLETED,
            objectivesExcerpt = "Define 1-year and 3-year career trajectories, identifying skill gaps and milestones.",
            fullObjectives = listOf(
                "Map desired roles against current strengths and competencies.",
                "Develop a 12-month milestone timeline for career entry.",
                "Identify key certifications and hands-on projects needed to stand out."
            ),
            materials = listOf(
                SessionMaterial("m4", "Career Pathway Planner", "PDF", "https://gmiportal.org/materials/session4_career.pdf", "940 KB", true)
            )
        ),
        MentorshipSession(
            sessionNumber = 5,
            title = "Develop Professional Skills Using SMART Goals",
            status = SessionStatus.COMPLETED,
            objectivesExcerpt = "Formulate Specific, Measurable, Achievable, Relevant, and Time-bound goals.",
            fullObjectives = listOf(
                "Differentiate between vague aspirations and actionable SMART goals.",
                "Draft 3 SMART goals covering technical, interpersonal, and leadership growth.",
                "Establish accountability metrics with your mentor."
            ),
            materials = listOf(
                SessionMaterial("m5", "SMART Goal Worksheet", "PDF", "https://gmiportal.org/materials/session5_smart.pdf", "780 KB", true)
            )
        ),
        MentorshipSession(
            sessionNumber = 6,
            title = "Prepare for a Job Interview",
            status = SessionStatus.COMPLETED,
            objectivesExcerpt = "Master the STAR method for behavioral questions and standard interview etiquette.",
            fullObjectives = listOf(
                "Deconstruct behavioral interview prompts using Situation, Task, Action, Result.",
                "Prepare 4 versatile STAR stories demonstrating resilience, teamwork, and problem solving.",
                "Formulate insightful reverse-questions for interviewers."
            ),
            materials = listOf(
                SessionMaterial("m6", "STAR Method Masterclass Sheet", "PDF", "https://gmiportal.org/materials/session6_star.pdf", "1.1 MB", true)
            )
        ),
        MentorshipSession(
            sessionNumber = 7,
            title = "Prepare for a Job Interview",
            status = SessionStatus.CURRENT,
            objectivesExcerpt = "Conduct a live mock interview, analyze responses, and refine delivery.",
            fullObjectives = listOf(
                "Participate in a 30-minute mock interview simulating a real technical/role screen.",
                "Receive constructive feedback on body language, tone, and conciseness.",
                "Practice answering tough questions, salary inquiries, and technical deep-dives.",
                "Create a final pre-interview checklist."
            ),
            materials = listOf(
                SessionMaterial("m7", "Mock Interview Evaluation Rubric", "PDF", "https://gmiportal.org/materials/session7_rubric.pdf", "1.3 MB", false)
            )
        ),
        MentorshipSession(
            sessionNumber = 8,
            title = "Understand Job Search Techniques",
            status = SessionStatus.LOCKED,
            objectivesExcerpt = "Navigate international job boards, hidden job markets, and recruiter channels.",
            fullObjectives = listOf(
                "Leverage specialized platforms (LinkedIn, Wellfound, GMI Partner Job Board).",
                "Learn strategies to bypass generic application queues through employee referrals.",
                "Organize job tracking spreadsheet with response status and follow-ups."
            ),
            materials = listOf(
                SessionMaterial("m8", "Job Search Tracker", "PDF", "https://gmiportal.org/materials/session8_tracker.pdf", "900 KB", false)
            )
        ),
        MentorshipSession(
            sessionNumber = 9,
            title = "Distinguish Yourself in the Workplace",
            status = SessionStatus.LOCKED,
            objectivesExcerpt = "Navigate onboarding, workplace culture, cross-functional collaboration, and feedback.",
            fullObjectives = listOf(
                "Understand implicit office culture and professional communication standards.",
                "Manage relationships with supervisors and peers effectively.",
                "Demonstrate proactive problem-solving during the first 90 days on the job."
            ),
            materials = listOf(
                SessionMaterial("m9", "First 90 Days Success Blueprint", "PDF", "https://gmiportal.org/materials/session9_success.pdf", "1.0 MB", false)
            )
        ),
        MentorshipSession(
            sessionNumber = 10,
            title = "Prepare for Global Business and Conclude Mentorship",
            status = SessionStatus.LOCKED,
            objectivesExcerpt = "Reflect on accomplishments, celebrate growth, unlock Student Certificate, and transition to alumni network.",
            fullObjectives = listOf(
                "Review growth across all 10 mentorship milestones.",
                "Receive official GMI Mentorship Graduation Certificate.",
                "Join global alumni community and access continued partner opportunities.",
                "Complete post-program feedback and celebration survey."
            ),
            materials = listOf(
                SessionMaterial("m10", "Program Graduation Guide & Alumni Access", "PDF", "https://gmiportal.org/materials/session10_alumni.pdf", "1.5 MB", false)
            )
        )
    )

    private val _sessions = MutableStateFlow<List<MentorshipSession>>(emptyList())
    override val sessions: StateFlow<List<MentorshipSession>> = _sessions.asStateFlow()

    private val _programOverview = MutableStateFlow(
        ProgramOverview(completedSessions = 6, totalSessions = 10, currentSessionNumber = 7)
    )
    override val programOverview: StateFlow<ProgramOverview> = _programOverview.asStateFlow()

    private val _outboundOfflineQueueCount = MutableStateFlow(0)
    override val outboundOfflineQueueCount: StateFlow<Int> = _outboundOfflineQueueCount.asStateFlow()

    init {
        // Load sessions from on-phone DB
        val loaded = localDb.getSessions() ?: initialSessions
        _sessions.value = loaded
        if (localDb.getSessions() == null) {
            localDb.saveSessions(loaded)
        }
        recomputeOverview(loaded)
        _outboundOfflineQueueCount.value = localDb.getOutboundQueue().size
    }

    private fun recomputeOverview(list: List<MentorshipSession>) {
        val completed = list.count { it.status == SessionStatus.COMPLETED }
        val current = list.find { it.status == SessionStatus.CURRENT }?.sessionNumber ?: (completed + 1).coerceAtMost(10)
        _programOverview.value = ProgramOverview(
            programName = "GMI Mentorship",
            completedSessions = completed,
            totalSessions = 10,
            currentSessionNumber = current
        )
    }

    override suspend fun refreshSessions() {
        val list = localDb.getSessions() ?: _sessions.value
        _sessions.value = list
        recomputeOverview(list)
    }

    override suspend fun getSessionDetail(sessionNumber: Int): MentorshipSession? {
        return _sessions.value.find { it.sessionNumber == sessionNumber }
    }

    override suspend fun markSessionComplete(sessionNumber: Int): Result<MentorshipSession> {
        val currentList = _sessions.value.toMutableList()
        val index = currentList.indexOfFirst { it.sessionNumber == sessionNumber }
        if (index == -1) return Result.failure(IllegalArgumentException("Session not found"))

        val target = currentList[index]
        if (target.status == SessionStatus.LOCKED) {
            return Result.failure(IllegalStateException("Cannot complete locked session."))
        }

        val completedTarget = target.copy(
            status = SessionStatus.COMPLETED,
            completedAt = Clock.System.now(),
            partnerNotified = true
        )
        currentList[index] = completedTarget

        // Unlock next session
        var nextSessionNum = sessionNumber + 1
        if (nextSessionNum <= 10) {
            val nextIndex = currentList.indexOfFirst { it.sessionNumber == nextSessionNum }
            if (nextIndex != -1) {
                currentList[nextIndex] = currentList[nextIndex].copy(status = SessionStatus.CURRENT)
            }
        }

        _sessions.value = currentList
        recomputeOverview(currentList)

        // Commit to ON-PHONE LOCAL DATABASE
        localDb.saveSessions(currentList)
        localDb.enqueueOutboundAction("POST /api/v1/program/sessions/$sessionNumber/complete")
        _outboundOfflineQueueCount.value = localDb.getOutboundQueue().size

        return Result.success(completedTarget)
    }

    override suspend fun syncOutboundQueue() {
        localDb.clearOutboundQueue()
        _outboundOfflineQueueCount.value = 0
    }

    fun resetToDefaults() {
        _sessions.value = initialSessions
        localDb.saveSessions(initialSessions)
        recomputeOverview(initialSessions)
    }
}

class PersistentCalendarRepository(
    private val localDb: GmiLocalDatabase
) : CalendarRepository {

    private val defaultSlots = listOf(
        TimeSlot("ts_1", "2026-09-18", "09:00", "10:00", SlotAvailabilityType.STUDENT_AVAILABLE, "9:00 AM - 10:00 AM (Student)", isUserSlot = true),
        TimeSlot("ts_2", "2026-09-18", "14:00", "15:00", SlotAvailabilityType.STUDENT_AVAILABLE, "2:00 PM - 3:00 PM (Student)", isUserSlot = true),
        TimeSlot("ts_3", "2026-09-19", "15:00", "16:00", SlotAvailabilityType.MENTOR_AVAILABLE, "3:00 PM - 4:00 PM (Mentor)", isPartnerSlot = true),
        TimeSlot("ts_4", "2026-09-22", "16:00", "17:00", SlotAvailabilityType.SUGGESTED, "4:00 PM - 5:00 PM (Suggested)", isUserSlot = false),
        TimeSlot("ts_5", "2026-09-25", "11:00", "12:00", SlotAvailabilityType.CONFIRMED, "11:00 AM - 12:00 PM (Confirmed)", isUserSlot = true, isPartnerSlot = true)
    )

    private val defaultSuggested = listOf(
        SuggestedMeeting(
            id = "sug_1",
            dateIso = "2026-09-22",
            startTimeIso = "16:00",
            endTimeIso = "17:00",
            displayTime = "Tuesday, Sep 22 • 4:00 PM - 5:00 PM (EAT)",
            sessionTitle = "Session 7 • Mock Interview & Feedback",
            suggestedByName = "Sarah Jenkins",
            suggestedByRole = UserRole.MENTOR,
            status = MeetingStatus.PENDING,
            meetingLink = "https://meet.google.com/gmi-mock-interview"
        )
    )

    private val _selectedTimezone = MutableStateFlow<String?>("Africa/Nairobi")
    override val selectedTimezone: StateFlow<String?> = _selectedTimezone.asStateFlow()

    private val _isTimezoneConfigured = MutableStateFlow(true)
    override val isTimezoneConfigured: StateFlow<Boolean> = _isTimezoneConfigured.asStateFlow()

    private val _currentMonthSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    override val currentMonthSlots: StateFlow<List<TimeSlot>> = _currentMonthSlots.asStateFlow()

    private val _suggestedMeetings = MutableStateFlow<List<SuggestedMeeting>>(emptyList())
    override val suggestedMeetings: StateFlow<List<SuggestedMeeting>> = _suggestedMeetings.asStateFlow()

    init {
        val savedSlots = localDb.getCalendarSlots() ?: defaultSlots
        _currentMonthSlots.value = savedSlots
        if (localDb.getCalendarSlots() == null) localDb.saveCalendarSlots(savedSlots)

        val savedSuggested = localDb.getSuggestedMeetings() ?: defaultSuggested
        _suggestedMeetings.value = savedSuggested
        if (localDb.getSuggestedMeetings() == null) localDb.saveSuggestedMeetings(savedSuggested)

        val tz = localDb.getUserProfile()?.timezone ?: "Africa/Nairobi"
        _selectedTimezone.value = tz
        _isTimezoneConfigured.value = tz.isNotBlank()
    }

    override suspend fun loadMonth(year: Int, month: Int) {}

    override suspend fun setTimezone(timezone: String) {
        _selectedTimezone.value = timezone
        _isTimezoneConfigured.value = timezone.isNotBlank()
        val user = localDb.getUserProfile()
        if (user != null) {
            localDb.saveUserProfile(user.copy(timezone = timezone))
        }
    }

    override suspend fun addAvailabilitySlot(dateIso: String, startHour: Int, endHour: Int) {
        val startFormatted = "${startHour.toString().padStart(2, '0')}:00"
        val endFormatted = "${endHour.toString().padStart(2, '0')}:00"
        val newSlot = TimeSlot(
            id = "slot_${dateIso}_${startHour}",
            dateIso = dateIso,
            startTimeIso = startFormatted,
            endTimeIso = endFormatted,
            type = SlotAvailabilityType.STUDENT_AVAILABLE,
            label = "$startFormatted - $endFormatted (You)",
            isUserSlot = true
        )
        val updated = _currentMonthSlots.value + newSlot
        _currentMonthSlots.value = updated

        // Commit to ON-PHONE LOCAL DATABASE
        localDb.saveCalendarSlots(updated)
        localDb.enqueueOutboundAction("POST /api/v1/calendar/availability ($dateIso $startFormatted)")
    }

    override suspend fun removeAvailabilitySlot(slotId: String) {
        val updated = _currentMonthSlots.value.filterNot { it.id == slotId }
        _currentMonthSlots.value = updated
        localDb.saveCalendarSlots(updated)
    }

    override suspend fun confirmMeeting(meetingId: String): Result<Unit> {
        val list = _suggestedMeetings.value.toMutableList()
        val index = list.indexOfFirst { it.id == meetingId }
        if (index != -1) {
            list[index] = list[index].copy(status = MeetingStatus.CONFIRMED)
            _suggestedMeetings.value = list
            localDb.saveSuggestedMeetings(list)
            localDb.enqueueOutboundAction("POST /api/v1/calendar/meetings/$meetingId/confirm")
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Meeting not found"))
    }

    override suspend fun declineMeeting(meetingId: String): Result<Unit> {
        val updated = _suggestedMeetings.value.filterNot { it.id == meetingId }
        _suggestedMeetings.value = updated
        localDb.saveSuggestedMeetings(updated)
        localDb.enqueueOutboundAction("POST /api/v1/calendar/meetings/$meetingId/decline")
        return Result.success(Unit)
    }

    override suspend fun suggestMeeting(
        dateIso: String,
        startTime: String,
        endTime: String,
        sessionNumber: Int
    ): Result<SuggestedMeeting> {
        val meeting = SuggestedMeeting(
            id = "sug_${System.currentTimeMillis()}",
            dateIso = dateIso,
            startTimeIso = startTime,
            endTimeIso = endTime,
            displayTime = "$dateIso • $startTime - $endTime",
            sessionTitle = "Session $sessionNumber • Meeting",
            suggestedByName = "Alex Mwangi",
            suggestedByRole = UserRole.STUDENT,
            status = MeetingStatus.PENDING
        )
        val updated = _suggestedMeetings.value + meeting
        _suggestedMeetings.value = updated
        localDb.saveSuggestedMeetings(updated)
        return Result.success(meeting)
    }
}

class PersistentMessageRepository(
    private val localDb: GmiLocalDatabase
) : MessageRepository {

    private val partnerMentorRecipient = MessageRecipient(
        id = "user_mentor_01",
        displayName = "Sarah Jenkins",
        roleTitle = "Mentor • Google",
        email = "sarah.jenkins@google.com",
        isGmiSupport = false
    )

    private val gmiSupportRecipient = MessageRecipient(
        id = "gmi_support",
        displayName = "GMI Support",
        roleTitle = "Mentorship Program Specialist",
        email = "info@globalmentorship.org",
        isGmiSupport = true
    )

    private val defaultThreads = listOf(
        MessageThread(
            id = "thread_1",
            subject = "Session 7 Mock Interview Preparation",
            recipient = partnerMentorRecipient,
            lastSnippet = "Thank you Sarah! I have updated my calendar availability for Tuesday afternoon.",
            lastTimestamp = "2026-09-17T11:15:00Z",
            relativeTime = "2 hours ago",
            unreadCount = 0,
            tab = MessageTab.INBOX,
            messages = listOf(
                ChatMessage(
                    id = "msg_1",
                    threadId = "thread_1",
                    senderId = "user_mentor_01",
                    senderName = "Sarah Jenkins",
                    senderRole = UserRole.MENTOR,
                    isFromSelf = false,
                    contentHtml = "<p>Hi Alex! Great work revising your resume in Session 2. Let's schedule our Session 7 mock interview soon.</p>",
                    timestampIso = "2026-09-17T11:00:00Z",
                    displayTime = "11:00 AM"
                ),
                ChatMessage(
                    id = "msg_2",
                    threadId = "thread_1",
                    senderId = "user_student_01",
                    senderName = "Alex Mwangi",
                    senderRole = UserRole.STUDENT,
                    isFromSelf = true,
                    contentHtml = "<p>Thank you Sarah! I have updated my calendar availability for Tuesday afternoon.</p>",
                    timestampIso = "2026-09-17T11:15:00Z",
                    displayTime = "11:15 AM"
                )
            )
        ),
        MessageThread(
            id = "thread_2",
            subject = "GMI Welcome & Onboarding Resources",
            recipient = gmiSupportRecipient,
            lastSnippet = "Welcome to the GMI Mentorship Program! Feel free to reach out if you have any questions.",
            lastTimestamp = "2026-09-10T10:00:00Z",
            relativeTime = "7 days ago",
            unreadCount = 0,
            tab = MessageTab.INBOX,
            messages = listOf(
                ChatMessage(
                    id = "msg_gmi_1",
                    threadId = "thread_2",
                    senderId = "gmi_support",
                    senderName = "GMI Support",
                    senderRole = UserRole.MENTOR,
                    isFromSelf = false,
                    contentHtml = "<p>Welcome to the <strong>Global Mentorship Initiative</strong>!</p>",
                    timestampIso = "2026-09-10T10:00:00Z",
                    displayTime = "Sep 10"
                )
            )
        )
    )

    private val _threads = MutableStateFlow<List<MessageThread>>(emptyList())
    private val _inboxThreads = MutableStateFlow<List<MessageThread>>(emptyList())
    override val inboxThreads: StateFlow<List<MessageThread>> = _inboxThreads.asStateFlow()

    private val _archivedThreads = MutableStateFlow<List<MessageThread>>(emptyList())
    override val archivedThreads: StateFlow<List<MessageThread>> = _archivedThreads.asStateFlow()

    private val _deletedThreads = MutableStateFlow<List<MessageThread>>(emptyList())
    override val deletedThreads: StateFlow<List<MessageThread>> = _deletedThreads.asStateFlow()

    private val _availableRecipients = MutableStateFlow(listOf(partnerMentorRecipient, gmiSupportRecipient))
    override val availableRecipients: StateFlow<List<MessageRecipient>> = _availableRecipients.asStateFlow()

    init {
        val loaded = localDb.getThreads() ?: defaultThreads
        _threads.value = loaded
        if (localDb.getThreads() == null) localDb.saveThreads(loaded)
        updateCategorized()
    }

    private fun updateCategorized() {
        val all = _threads.value
        _inboxThreads.value = all.filter { it.tab == MessageTab.INBOX }
        _archivedThreads.value = all.filter { it.tab == MessageTab.ARCHIVED }
        _deletedThreads.value = all.filter { it.tab == MessageTab.DELETED }
    }

    override suspend fun refreshThreads() {
        updateCategorized()
    }

    override suspend fun getThreadMessages(threadId: String): List<ChatMessage> {
        return _threads.value.find { it.id == threadId }?.messages ?: emptyList()
    }

    override suspend fun sendMessage(threadId: String, contentHtml: String): Result<ChatMessage> {
        val current = _threads.value.toMutableList()
        val index = current.indexOfFirst { it.id == threadId }
        if (index == -1) return Result.failure(IllegalArgumentException("Thread not found"))

        val thread = current[index]
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            threadId = threadId,
            senderId = "user_student_01",
            senderName = "Alex Mwangi",
            senderRole = UserRole.STUDENT,
            isFromSelf = true,
            contentHtml = contentHtml,
            timestampIso = "2026-09-17T12:00:00Z",
            displayTime = "Just now"
        )
        val updatedThread = thread.copy(
            messages = thread.messages + newMsg,
            lastSnippet = contentHtml.replace(Regex("<[^>]*>"), "").take(80),
            lastTimestamp = "2026-09-17T12:00:00Z",
            relativeTime = "Just now"
        )
        current[index] = updatedThread
        _threads.value = current
        updateCategorized()

        // Persist to ON-PHONE LOCAL DATABASE
        localDb.saveThreads(current)
        localDb.enqueueOutboundAction("POST /api/v1/messages/threads/$threadId/messages")

        return Result.success(newMsg)
    }

    override suspend fun createThread(
        recipientId: String,
        subject: String,
        contentHtml: String
    ): Result<MessageThread> {
        val recipient = _availableRecipients.value.find { it.id == recipientId } ?: partnerMentorRecipient
        val threadId = "thread_${System.currentTimeMillis()}"
        val initialMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            threadId = threadId,
            senderId = "user_student_01",
            senderName = "Alex Mwangi",
            senderRole = UserRole.STUDENT,
            isFromSelf = true,
            contentHtml = contentHtml,
            timestampIso = "2026-09-17T12:00:00Z",
            displayTime = "Just now"
        )
        val newThread = MessageThread(
            id = threadId,
            subject = subject,
            recipient = recipient,
            lastSnippet = contentHtml.replace(Regex("<[^>]*>"), "").take(80),
            lastTimestamp = "2026-09-17T12:00:00Z",
            relativeTime = "Just now",
            tab = MessageTab.INBOX,
            messages = listOf(initialMsg)
        )
        val updated = listOf(newThread) + _threads.value
        _threads.value = updated
        updateCategorized()

        // Persist to ON-PHONE LOCAL DATABASE
        localDb.saveThreads(updated)
        localDb.enqueueOutboundAction("POST /api/v1/messages/threads")

        return Result.success(newThread)
    }

    override suspend fun archiveThread(threadId: String) {
        val updated = _threads.value.map {
            if (it.id == threadId) it.copy(tab = MessageTab.ARCHIVED) else it
        }
        _threads.value = updated
        updateCategorized()
        localDb.saveThreads(updated)
    }

    override suspend fun unarchiveThread(threadId: String) {
        val updated = _threads.value.map {
            if (it.id == threadId) it.copy(tab = MessageTab.INBOX) else it
        }
        _threads.value = updated
        updateCategorized()
        localDb.saveThreads(updated)
    }

    override suspend fun deleteThread(threadId: String) {
        val updated = _threads.value.map {
            if (it.id == threadId) it.copy(tab = MessageTab.DELETED) else it
        }
        _threads.value = updated
        updateCategorized()
        localDb.saveThreads(updated)
    }

    override suspend fun restoreThread(threadId: String) {
        val updated = _threads.value.map {
            if (it.id == threadId) it.copy(tab = MessageTab.INBOX) else it
        }
        _threads.value = updated
        updateCategorized()
        localDb.saveThreads(updated)
    }
}

class PersistentNotificationRepository(
    private val localDb: GmiLocalDatabase
) : NotificationRepository {

    private val defaultNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            title = "Google EMEA Tech Scholarship Opportunity",
            body = "We are pleased to announce partner applications are open for students in the GMI network. Review eligibility, prepare your resume from Session 2, and submit before October 15.",
            timestampIso = "2026-09-17T14:00:00Z",
            relativeTime = "1 hour ago",
            isRead = false,
            category = NotificationCategory.GMI_BROADCAST,
            externalUrl = "https://globalmentorship.org/scholarships/emea",
            allowsReplyToGmi = true
        ),
        NotificationItem(
            id = "notif_2",
            title = "Sessions 4–6 complete – 2nd check-in",
            body = "Congratulations on reaching the halfway mark! Your mentor and GMI Program Specialist have verified your progress.",
            timestampIso = "2026-09-16T09:30:00Z",
            relativeTime = "1 day ago",
            isRead = false,
            category = NotificationCategory.PROGRAM_CHECKIN,
            allowsReplyToGmi = true
        ),
        NotificationItem(
            id = "notif_3",
            title = "Meeting confirmed: Session 7 Mock Interview",
            body = "Your mentor Sarah Jenkins confirmed your meeting for Tuesday, Sep 22 at 4:00 PM EAT.",
            timestampIso = "2026-09-15T16:20:00Z",
            relativeTime = "2 days ago",
            isRead = true,
            category = NotificationCategory.CALENDAR_UPDATE,
            allowsReplyToGmi = false
        )
    )

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    override val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        val loaded = localDb.getNotifications() ?: defaultNotifications
        _notifications.value = loaded
        if (localDb.getNotifications() == null) localDb.saveNotifications(loaded)
        recompute()
    }

    private fun recompute() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }

    override suspend fun refresh() {
        recompute()
    }

    override suspend fun markAsRead(id: String) {
        val updated = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        _notifications.value = updated
        recompute()
        localDb.saveNotifications(updated)
    }

    override suspend fun markAllAsRead() {
        val updated = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = updated
        recompute()
        localDb.saveNotifications(updated)
    }

    override suspend fun deleteNotification(id: String) {
        val updated = _notifications.value.filterNot { it.id == id }
        _notifications.value = updated
        recompute()
        localDb.saveNotifications(updated)
    }
}
