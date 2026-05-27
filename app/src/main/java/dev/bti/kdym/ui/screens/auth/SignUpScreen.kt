package dev.bti.kdym.ui.screens.auth

import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.Church
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

enum class SignUpPhase {
    PERSONAL_INFO,
    CREDENTIALS,
    CHURCH_SELECTION
}

@Composable
fun SignUpScreen(
    onSignUp: (String, String, String, String?, String?, String?, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    var phase by remember { mutableStateOf(SignUpPhase.PERSONAL_INFO) }
    
    // Phase 1 Data
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    // Phase 2 Data
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Phase 3 Data
    var selectedChurch by remember { mutableStateOf<Church?>(null) }
    var churchSearchQuery by remember { mutableStateOf("") }

    val isLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val churches by viewModel.churches.collectAsState()

    val filteredChurches = remember(churches, churchSearchQuery) {
        if (churchSearchQuery.isEmpty()) churches
        else churches.filter { 
            it.name.contains(churchSearchQuery, ignoreCase = true) || 
            it.city.contains(churchSearchQuery, ignoreCase = true) ||
            it.pastorName.contains(churchSearchQuery, ignoreCase = true)
        }
    }

    OutpourBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Back Button & Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (phase != SignUpPhase.PERSONAL_INFO) {
                        IconButton(
                            onClick = { 
                                when (phase) {
                                    SignUpPhase.PERSONAL_INFO -> onNavigateToLogin()
                                    SignUpPhase.CREDENTIALS -> phase = SignUpPhase.PERSONAL_INFO
                                    SignUpPhase.CHURCH_SELECTION -> phase = SignUpPhase.CREDENTIALS
                                }
                            },
                            modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    
                    SignUpProgressIndicator(phase)
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedContent(
                    targetState = phase,
                    modifier = Modifier.weight(1f),
                    label = "signup_phase"
                ) { currentPhase ->
                    when (currentPhase) {
                        SignUpPhase.PERSONAL_INFO -> PersonalInfoPhase(
                            firstName = firstName,
                            onFirstNameChange = { firstName = it },
                            lastName = lastName,
                            onLastNameChange = { lastName = it },
                            username = username,
                            onUsernameChange = { username = it },
                            onContinue = { phase = SignUpPhase.CREDENTIALS }
                        )
                        SignUpPhase.CREDENTIALS -> CredentialsPhase(
                            email = email,
                            onEmailChange = { email = it },
                            password = password,
                            onPasswordChange = { password = it },
                            phoneNumber = phoneNumber,
                            onPhoneNumberChange = { phoneNumber = it },
                            onContinue = { phase = SignUpPhase.CHURCH_SELECTION }
                        )
                        SignUpPhase.CHURCH_SELECTION -> ChurchSelectionPhase(
                            searchQuery = churchSearchQuery,
                            onSearchQueryChange = { churchSearchQuery = it },
                            churches = filteredChurches,
                            selectedChurch = selectedChurch,
                            onChurchSelected = { selectedChurch = it },
                            onSignUp = { 
                                onSignUp(firstName, lastName, username, phoneNumber, selectedChurch?.id, selectedChurch?.name, email, password)
                            },
                            isLoading = isLoading,
                            authError = authError
                        )
                    }
                }
                
                if (phase == SignUpPhase.PERSONAL_INFO) {
                    Text(
                        text = "Already have an account? Sign in",
                        color = Color(0xFF22D3EE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier
                            .clickable { onNavigateToLogin() }
                            .padding(bottom = 32.dp)
                    )
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .align(Alignment.TopCenter),
                    color = RedAccent,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun SignUpProgressIndicator(phase: SignUpPhase) {
    Row(
        modifier = Modifier.fillMaxWidth().height(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val phases = SignUpPhase.entries
        phases.forEach { p ->
            val isActive = p.ordinal <= phase.ordinal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isActive) Color(0xFF22D3EE) else Color.White.copy(0.1f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun PersonalInfoPhase(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "JOIN KDYM",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = "Start with your real name and a clean username.",
            color = TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontFamily = QuickSandFontFamily
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        CommandInputField(
            value = firstName,
            onValueChange = onFirstNameChange,
            placeholder = "First Name",
            icon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(12.dp))
        CommandInputField(
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = "Last Name",
            icon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(12.dp))
        CommandInputField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = "Username",
            icon = Icons.Default.AlternateEmail
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Keep it clean card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color.White.copy(0.05f)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "KEEP IT CLEAN",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Your username will be visible in community areas and Tribe Wars. Please use a respectful name.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = QuickSandFontFamily,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        val canContinue = firstName.isNotBlank() && lastName.isNotBlank() && username.isNotBlank()

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canContinue) Color.White else Color.White.copy(0.1f),
                contentColor = if (canContinue) Color.Black else Color.White.copy(0.3f)
            ),
            shape = RoundedCornerShape(28.dp),
            enabled = canContinue
        ) {
            Text(text = "CONTINUE", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun CredentialsPhase(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "CREDENTIALS",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = "Set your login email and a secure password.",
            color = TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontFamily = QuickSandFontFamily
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        CommandInputField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email Address",
            icon = Icons.Default.Email
        )
        Spacer(modifier = Modifier.height(12.dp))
        CommandInputField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Password",
            icon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(12.dp))
        CommandInputField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = "Phone Number",
            icon = Icons.Default.Phone,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PasswordStrengthMeter(password)
        
        Spacer(modifier = Modifier.weight(1f))
        
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordStrong = password.length >= 8 && password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() }
        val isPhoneValid = phoneNumber.length >= 10
        val canContinue = isEmailValid && isPasswordStrong && isPhoneValid

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canContinue) Color.White else Color.White.copy(0.1f),
                contentColor = if (canContinue) Color.Black else Color.White.copy(0.3f)
            ),
            shape = RoundedCornerShape(28.dp),
            enabled = canContinue
        ) {
            Text(text = "CONTINUE", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun PasswordStrengthMeter(password: String) {
    val hasMinLength = password.length >= 8
    val hasNumber = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "PASSWORD STRENGTH", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            val strength = listOf(hasMinLength, hasNumber, hasSpecial).count { it }
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            if (i < strength) {
                                when (strength) {
                                    1 -> Color(0xFFEF4444)
                                    2 -> Color(0xFFEAB308)
                                    else -> Color(0xFF10B981)
                                }
                            } else Color.White.copy(0.1f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StrengthRequirement("At least 8 characters", hasMinLength)
            StrengthRequirement("At least 1 number", hasNumber)
            StrengthRequirement("At least 1 special character", hasSpecial)
        }
    }
}

@Composable
fun StrengthRequirement(label: String, isMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Circle,
            contentDescription = null,
            tint = if (isMet) Color(0xFF10B981) else TextSecondary.copy(0.3f),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = if (isMet) Color.White else TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
    }
}

@Composable
fun ChurchSelectionPhase(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    churches: List<Church>,
    selectedChurch: Church?,
    onChurchSelected: (Church) -> Unit,
    onSignUp: () -> Unit,
    isLoading: Boolean,
    authError: String?
) {
    Column {
        Text(
            text = "YOUR CHURCH",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Connect your account to your church family.",
            color = TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontFamily = QuickSandFontFamily,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        CommandInputField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Search church, city, or pastor",
            icon = Icons.Default.Search
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(churches, key = { it.id }) { church ->
                val isSelected = selectedChurch?.id == church.id
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChurchSelected(church) },
                    backgroundColor = if (isSelected) Color(0xFF22D3EE).copy(0.15f) else Color.Black.copy(0.3f),
                    borderColor = if (isSelected) Color(0xFF22D3EE) else Color.White.copy(0.1f),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).background(Color.White.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Church, contentDescription = null, tint = Color.White.copy(0.6f))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = church.name,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "Pastor ${church.pastorName} • ${church.city}, ${church.state}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                }
            }
        }
        
        if (authError != null) {
            Text(
                text = authError,
                color = RedAccent,
                fontSize = 14.sp,
                fontFamily = QuickSandFontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onSignUp,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedChurch != null) Color.White else Color.White.copy(0.1f),
                contentColor = if (selectedChurch != null) Color.Black else Color.White.copy(0.3f)
            ),
            shape = RoundedCornerShape(28.dp),
            enabled = selectedChurch != null && !isLoading
        ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "CREATE ACCOUNT", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
        }
    }
}
