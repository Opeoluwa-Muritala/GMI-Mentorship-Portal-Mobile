package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@Composable
fun LoginScreen(
    isBiometricAvailable: Boolean,
    onLoginSubmit: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onBiometricUnlock: () -> Unit,
    onForgotPassword: (String) -> Unit,
    onOpenGmiWebsite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var email by remember { mutableStateOf("alex.mwangi@student.uonbi.ac.ke") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var captchaVerified by remember { mutableStateOf(false) }
    var showCaptchaDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GmiNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo & Title
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GMI",
                    color = GmiNavy,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GMI Mentorship Portal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Global Mentorship Initiative",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Login Card
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Please sign in with the email address you used to register with GMI.",
                        fontSize = 12.sp,
                        color = GmiTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(strings.email) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = GmiNavy) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GmiNavy) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = GmiPrimaryBlue)
                            )
                            Text(text = "Remember me", fontSize = 12.sp, color = GmiTextPrimary)
                        }

                        Text(
                            text = "Forgot password?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GmiPrimaryBlue,
                            modifier = Modifier.clickable { onForgotPassword(email) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Security Check (CAPTCHA verification)
                    Surface(
                        color = if (captchaVerified) GmiGreenLight else GmiBackground,
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCaptchaDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (captchaVerified) Icons.Default.CheckCircle else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (captchaVerified) GmiGreenCompleted else GmiNavy
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (captchaVerified) "Security Check Verified ✓" else "Tap to complete security check (CAPTCHA)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (captchaVerified) GmiGreenCompleted else GmiNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            if (!captchaVerified) {
                                showCaptchaDialog = true
                            } else {
                                onLoginSubmit(email, password, rememberMe)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = "Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (isBiometricAvailable) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onBiometricUnlock,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = GmiNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Unlock with Biometrics", color = GmiNavy, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // No in-app registration: link out to GMI's apply / become-a-mentor pages
            Text(
                text = "Don't have an account?",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Apply as a Student ↗",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onOpenGmiWebsite("https://globalmentorship.org/apply") }
                )
                Text(
                    text = "Become a Mentor ↗",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onOpenGmiWebsite("https://globalmentorship.org/mentor") }
                )
            }
        }
    }

    // Interactive CAPTCHA Simulation Modal
    if (showCaptchaDialog) {
        AlertDialog(
            onDismissRequest = { showCaptchaDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = GmiPrimaryBlue,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("GMI Security Verification", fontWeight = FontWeight.Bold, color = GmiNavy)
            },
            text = {
                Text(
                    "To verify you are not an automated system, please confirm this security check.",
                    fontSize = 13.sp,
                    color = GmiTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        captchaVerified = true
                        showCaptchaDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue)
                ) {
                    Text("I'm not a robot", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCaptchaDialog = false }) {
                    Text("Cancel", color = GmiTextSecondary)
                }
            }
        )
    }
}
