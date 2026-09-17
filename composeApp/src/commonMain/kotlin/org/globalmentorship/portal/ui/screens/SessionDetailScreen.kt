package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import org.globalmentorship.portal.domain.models.MentorshipSession
import org.globalmentorship.portal.domain.models.SessionMaterial
import org.globalmentorship.portal.domain.models.SessionStatus
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    session: MentorshipSession?,
    onBackClicked: () -> Unit,
    onOpenMaterial: (SessionMaterial) -> Unit,
    onMarkCompleteConfirmed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (session == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Session ${session.sessionNumber}",
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GmiSurface)
            )
        },
        bottomBar = {
            if (session.status == SessionStatus.CURRENT) {
                Surface(
                    color = GmiSurface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GmiGreenCompleted),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.markSessionComplete,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title & Status
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GmiSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = session.programName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = GmiPrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (session.status == SessionStatus.COMPLETED) {
                            Surface(
                                color = GmiGreenLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GmiGreenCompleted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Session Completed • Partner Notified",
                                        color = GmiGreenCompleted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Full Objectives
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GmiSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.sessionObjectives,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        session.fullObjectives.forEachIndexed { index, obj ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GmiBlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = GmiNavy,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = obj,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GmiTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Downloadable Materials
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GmiSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.downloadableMaterials,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (session.materials.isEmpty()) {
                            Text(
                                text = "No worksheets attached for this session.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GmiTextSecondary
                            )
                        } else {
                            session.materials.forEach { material ->
                                Surface(
                                    color = GmiBackground,
                                    shape = RoundedCornerShape(8.dp),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onOpenMaterial(material) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = GmiPrimaryBlue,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = material.title,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp,
                                                    color = GmiTextPrimary
                                                )
                                                Text(
                                                    text = "${material.fileType} • ${material.sizeDisplay} ${if (material.isCachedLocally) "• Cached Offline" else ""}",
                                                    fontSize = 11.sp,
                                                    color = if (material.isCachedLocally) GmiGreenCompleted else GmiTextSecondary
                                                )
                                            }
                                        }
                                        IconButton(onClick = { onOpenMaterial(material) }) {
                                            Icon(
                                                imageVector = if (material.isCachedLocally) Icons.Default.Visibility else Icons.Default.Download,
                                                contentDescription = "View Material",
                                                tint = GmiPrimaryBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog restating the program integrity warning
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = strings.markCompleteConfirmTitle,
                    fontWeight = FontWeight.Bold,
                    color = GmiNavy
                )
            },
            text = {
                Text(
                    text = strings.markCompleteConfirmMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GmiTextPrimary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onMarkCompleteConfirmed(session.sessionNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GmiGreenCompleted)
                ) {
                    Text(text = strings.confirm, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmDialog = false }) {
                    Text(text = strings.cancel, color = GmiNavy)
                }
            }
        )
    }
}
