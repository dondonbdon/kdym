package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.ModerationReport
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModerationReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val reports by viewModel.moderationReports.collectAsState()

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "MODERATION REPORTS",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            if (reports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No reports found.",
                        color = TextSecondary,
                        fontFamily = QuickSandFontFamily
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(reports) { report ->
                        ReportCard(
                            report = report,
                            onUpdateStatus = { status ->
                                viewModel.updateReportStatus(report.id, status)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    report: ModerationReport,
    onUpdateStatus: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val dateString = report.createdAt?.let { dateFormat.format(it.toDate()) } ?: "Recently"

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (report.status) {
                        "open" -> Color(0xFFEF4444).copy(0.2f)
                        "resolved" -> Color(0xFF10B981).copy(0.2f)
                        else -> Color.White.copy(0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = report.status.uppercase(),
                        color = when (report.status) {
                            "open" -> Color(0xFFEF4444)
                            "resolved" -> Color(0xFF10B981)
                            else -> Color.White
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = QuickSandFontFamily
                    )
                }
                Text(
                    text = dateString,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = report.reason.uppercase(),
                color = Color(0xFF22D3EE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )

            if (report.type == "groupMessage") {
                Spacer(modifier = Modifier.height(8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White.copy(0.05f),
                    cornerRadius = 12.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = report.messageSenderName,
                                color = Color.White.copy(0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.messageText,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }

            if (report.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "REPORTER DETAILS:",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = report.details,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reported by ${report.reporterName}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            if (report.status == "open") {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onUpdateStatus("dismissed") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("DISMISS", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                    }
                    Button(
                        onClick = { onUpdateStatus("resolved") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("RESOLVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                    }
                }
            }
        }
    }
}
