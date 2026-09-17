package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.*
import org.globalmentorship.portal.localization.formatArgs
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    inboxThreads: List<MessageThread>,
    archivedThreads: List<MessageThread>,
    deletedThreads: List<MessageThread>,
    availableRecipients: List<MessageRecipient>,
    onThreadClicked: (String) -> Unit,
    onCreateThread: (recipientId: String, subject: String, contentHtml: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(MessageTab.INBOX) }
    var searchQuery by remember { mutableStateOf("") }
    var showNewConversationModal by remember { mutableStateOf(false) }

    val currentThreads = when (selectedTab) {
        MessageTab.INBOX -> inboxThreads
        MessageTab.ARCHIVED -> archivedThreads
        MessageTab.DELETED -> deletedThreads
    }.filter {
        searchQuery.isBlank() ||
                it.subject.contains(searchQuery, ignoreCase = true) ||
                it.recipient.displayName.contains(searchQuery, ignoreCase = true) ||
                it.lastSnippet.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == MessageTab.INBOX) {
                ExtendedFloatingActionButton(
                    onClick = { showNewConversationModal = true },
                    containerColor = GmiPrimaryBlue,
                    contentColor = Color.White,
                    icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) },
                    text = { Text(strings.newConversation, fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(GmiBackground)
                .padding(paddingValues)
        ) {
            // Header Tabs: Inbox / Archived / Deleted
            Surface(
                color = GmiSurface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        containerColor = GmiSurface,
                        contentColor = GmiNavy
                    ) {
                        Tab(
                            selected = selectedTab == MessageTab.INBOX,
                            onClick = { selectedTab = MessageTab.INBOX },
                            text = { Text(strings.tabInbox, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = selectedTab == MessageTab.ARCHIVED,
                            onClick = { selectedTab = MessageTab.ARCHIVED },
                            text = { Text(strings.tabArchived, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = selectedTab == MessageTab.DELETED,
                            onClick = { selectedTab = MessageTab.DELETED },
                            text = { Text(strings.tabDeleted, fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        placeholder = { Text(strings.searchConversations, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GmiTextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GmiPrimaryBlue,
                            unfocusedBorderColor = GmiBorder
                        )
                    )
                }
            }

            // Conversations List or Empty State
            if (currentThreads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = GmiNavy.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = strings.emptyConversations,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GmiTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentThreads, key = { it.id }) { thread ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GmiSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThreadClicked(thread.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (thread.recipient.isGmiSupport) GmiOrangeLight else GmiBlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = thread.recipient.displayName.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (thread.recipient.isGmiSupport) GmiOrangeCurrent else GmiNavy
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = thread.recipient.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = GmiNavy
                                        )
                                        Text(
                                            text = thread.relativeTime,
                                            fontSize = 11.sp,
                                            color = GmiTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = thread.subject,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = GmiTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = thread.lastSnippet,
                                        fontSize = 12.sp,
                                        color = GmiTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Conversation Full-Screen Modal
    if (showNewConversationModal) {
        NewConversationModal(
            recipients = availableRecipients,
            onDismiss = { showNewConversationModal = false },
            onSubmit = { recipientId, subject, message ->
                onCreateThread(recipientId, subject, message)
                showNewConversationModal = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewConversationModal(
    recipients: List<MessageRecipient>,
    onDismiss: () -> Unit,
    onSubmit: (recipientId: String, subject: String, message: String) -> Unit
) {
    val strings = LocalStrings.current
    var selectedRecipientId by remember { mutableStateOf(recipients.firstOrNull()?.id ?: "") }
    var subjectText by remember { mutableStateOf("") }
    var messageBody by remember { mutableStateOf("") }
    var isPreviewMode by remember { mutableStateOf(false) }

    val isSubjectValid = subjectText.isNotBlank() && subjectText.length <= 200
    val isMessageValid = messageBody.isNotBlank() && messageBody.length <= 10000
    val isFormValid = isSubjectValid && isMessageValid && selectedRecipientId.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GmiSurface,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.newConversation,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fixed Single-Select Recipient Picker
                Text(
                    text = strings.recipient,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = GmiNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                recipients.forEach { rec ->
                    val isSelected = selectedRecipientId == rec.id
                    Surface(
                        color = if (isSelected) GmiBlueLight else GmiBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedRecipientId = rec.id }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedRecipientId = rec.id },
                                colors = RadioButtonDefaults.colors(selectedColor = GmiPrimaryBlue)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = rec.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GmiNavy
                                )
                                Text(
                                    text = "${rec.roleTitle} • ${rec.email}",
                                    fontSize = 11.sp,
                                    color = GmiTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subject Field (Max 200 chars with live counter)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.subject} *",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = GmiNavy
                    )
                    Text(
                        text = strings.subjectCharLimit.formatArgs(subjectText.length),
                        fontSize = 11.sp,
                        color = if (subjectText.length > 200) Color.Red else GmiTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = subjectText,
                    onValueChange = { if (it.length <= 220) subjectText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter conversation subject...", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Message Body with Rich-Text Formatting Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.messageContent} *",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = GmiNavy
                    )
                    Text(
                        text = strings.messageCharLimit.formatArgs(messageBody.length),
                        fontSize = 11.sp,
                        color = if (messageBody.length > 10000) Color.Red else GmiTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Formatting Toolbar: Bold, Italic, Underline, Emoji, Link, Preview toggle
                Surface(
                    color = GmiGrayLight,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { messageBody += "<strong>text</strong>" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { messageBody += "<em>text</em>" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { messageBody += "<u>text</u>" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatUnderlined, contentDescription = "Underline", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { messageBody += "🤝" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.EmojiEmotions, contentDescription = "Emoji", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { messageBody += "<a href='https://'>link</a>" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = "Link", modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        TextButton(
                            onClick = { isPreviewMode = !isPreviewMode },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (isPreviewMode) "Edit" else "Preview",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GmiPrimaryBlue
                            )
                        }
                    }
                }

                if (isPreviewMode) {
                    Surface(
                        color = GmiBackground,
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 12.dp)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = messageBody.replace(Regex("<[^>]*>"), ""),
                                fontSize = 13.sp,
                                color = GmiTextPrimary
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = messageBody,
                        onValueChange = { messageBody = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 12.dp),
                        placeholder = { Text(strings.typeMessagePlaceholder, fontSize = 13.sp) },
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
                }

                // Modal Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(strings.cancel, color = GmiNavy)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(selectedRecipientId, subjectText, messageBody) },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue)
                    ) {
                        Text(strings.createConversation, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
