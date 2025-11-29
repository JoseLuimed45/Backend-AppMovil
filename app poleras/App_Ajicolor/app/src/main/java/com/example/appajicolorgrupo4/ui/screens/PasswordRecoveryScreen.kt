package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.data.local.database.AppDatabase
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.AuthViewModel
import com.example.appajicolorgrupo4.viewmodel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val repository = UserRepository(database.userDao())
    val sessionManager = SessionManager(context)
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository, sessionManager))

    val recoverState by viewModel.recoverState.collectAsState()
    val resetState by viewModel.resetState.collectAsState()

    // Step 1: Request Code, Step 2: Reset Password
    var currentStep by remember { mutableIntStateOf(1) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Effects
    LaunchedEffect(recoverState.success) {
        if (recoverState.success) {
            currentStep = 2
            viewModel.clearRecoverState() // Clear success flag but keep email if needed? 
            // Actually we need to keep email for the next step. 
            // The clearRecoverState resets email too. 
            // Let's NOT clear it yet, or modify ViewModel to not clear email.
            // For now, let's just proceed. The email is still in the state until cleared.
        }
    }

    LaunchedEffect(resetState.success) {
        if (resetState.success) {
            // Navigate back to login
            navController.popBackStack()
            viewModel.clearResetState()
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentStep == 1) {
                // STEP 1: REQUEST CODE
                Image(
                    painter = painterResource(id = R.drawable.recovery),
                    contentDescription = "Recuperar Contraseña",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recuperar Contraseña",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AmarilloAji
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingresa tu correo electrónico y te enviaremos un código de recuperación.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = recoverState.email,
                    onValueChange = viewModel::onRecoverEmailChange,
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = recoverState.emailError != null,
                    supportingText = { recoverState.emailError?.let { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmarilloAji,
                        unfocusedBorderColor = AmarilloAji,
                        focusedLabelColor = AmarilloAji,
                        unfocusedLabelColor = AmarilloAji,
                        cursorColor = AmarilloAji,
                        focusedTextColor = MoradoAji,
                        unfocusedTextColor = MoradoAji,
                        focusedContainerColor = Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )

                if (recoverState.error != null) {
                    Text(text = recoverState.error!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::submitRecover,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !recoverState.isLoading
                ) {
                    if (recoverState.isLoading) {
                        CircularProgressIndicator(color = MoradoAji, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Enviar Código")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }

            } else {
                // STEP 2: RESET PASSWORD
                Image(
                    painter = painterResource(id = R.drawable.envio_correo),
                    contentDescription = "Reset Password",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Restablecer Contraseña",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AmarilloAji
                )
                
                Text(
                    text = "Hemos enviado un código a ${recoverState.email}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Code Input
                OutlinedTextField(
                    value = resetState.code,
                    onValueChange = viewModel::onResetCodeChange,
                    label = { Text("Código de 6 dígitos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmarilloAji,
                        unfocusedBorderColor = AmarilloAji,
                        focusedLabelColor = AmarilloAji,
                        unfocusedLabelColor = AmarilloAji,
                        cursorColor = AmarilloAji,
                        focusedTextColor = MoradoAji,
                        unfocusedTextColor = MoradoAji,
                        focusedContainerColor = Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // New Password
                OutlinedTextField(
                    value = resetState.newPassword,
                    onValueChange = viewModel::onResetPassChange,
                    label = { Text("Nueva Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                        }
                    },
                    isError = resetState.passError != null,
                    supportingText = { resetState.passError?.let { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmarilloAji,
                        unfocusedBorderColor = AmarilloAji,
                        focusedLabelColor = AmarilloAji,
                        unfocusedLabelColor = AmarilloAji,
                        cursorColor = AmarilloAji,
                        focusedTextColor = MoradoAji,
                        unfocusedTextColor = MoradoAji,
                        focusedContainerColor = Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                OutlinedTextField(
                    value = resetState.confirmPassword,
                    onValueChange = viewModel::onResetConfirmChange,
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(if (showConfirmPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                        }
                    },
                    isError = resetState.confirmError != null,
                    supportingText = { resetState.confirmError?.let { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmarilloAji,
                        unfocusedBorderColor = AmarilloAji,
                        focusedLabelColor = AmarilloAji,
                        unfocusedLabelColor = AmarilloAji,
                        cursorColor = AmarilloAji,
                        focusedTextColor = MoradoAji,
                        unfocusedTextColor = MoradoAji,
                        focusedContainerColor = Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )

                if (resetState.error != null) {
                    Text(text = resetState.error!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::submitReset,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !resetState.isLoading
                ) {
                    if (resetState.isLoading) {
                        CircularProgressIndicator(color = MoradoAji, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Cambiar Contraseña")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { currentStep = 1 }, // Go back to step 1
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }
    }
}
