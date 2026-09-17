package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.ChatMessage
import org.globalmentorship.portal.domain.models.MessageThread
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageThreadScreen(
    thread: MessageThread?,
    messages: List<ChatMessage>,
    onBackClicked: () -> Unit,
    onSendMessage: (String) -> Unit,
    onArchiveThread: () -> Unit,
    onDeleteThread: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var replyText by remember { mutableStateOf("") }

    if (thread == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = thread.subject,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GmiNavy,
                            maxLines = 1
                        )
                        Text(
                            text = "${thread.recipient.displayName} • ${thread.recipient.roleTitle}",
                            fontSize = 12.sp,
                            color = GmiTextSecondary,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GmiNavy
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onArchiveThread) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive",
                            tint = GmiNavy
                        )
                    }
                    IconButton(onClick = onDeleteThread) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color(0xFFC62828)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GmiSurface)
            )
        },
        bottomBar = {
            // Reply composer docking above keyboard with formatting tools
            Surface(
                color = GmiSurface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Formatting shortcuts row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { replyText += "<strong>bold</strong>" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { replyText += "<em>italic</em>" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { replyText += "✨" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.EmojiEmotions, contentDescription = "Emoji", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Input field & Send button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(strings.typeMessagePlaceholder, fontSize = 13.sp) },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GmiPrimaryBlue,
                                unfocusedBorderColor = GmiBorder
                            ),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSendMessage(replyText)
                                    replyText = ""
                                }
                            },
                            enabled = replyText.isNotBlank(),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (replyText.isNotBlank()) GmiPrimaryBlue else GmiGrayLight)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = strings.send,
                                tint = if (replyText.isNotBlank()) Color.White else GmiGrayLocked,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(GmiBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isSelf = msg.isFromSelf
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
                ) {
                    Text(
                        text = "${msg.senderName} • ${msg.displayTime}",
                        fontSize = 11.sp,
                        color = GmiTextSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )

                    Surface(
                        color = if (isSelf) GmiPrimaryBlue else GmiSurface,
                        contentColor = if (isSelf) Color.White else GmiTextPrimary,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isSelf) 16.dp else 4.dp,
                            bottomEnd = if (isSelf) 4.dp else 16.dp
                        ),
                        border = if (isSelf) null else CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        // Strip HTML tags for clean bubble rendering
                        val cleanContent = msg.contentHtml.replace(Regex("<[^>]*>"), "")
                        Text(
                            text = cleanContent,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
