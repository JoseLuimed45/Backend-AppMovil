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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.AuthViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel

@Composable
fun PasswordRecoveryScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    val recoverState by authViewModel.recoverState.collectAsState()
    val resetState by authViewModel.resetState.collectAsState()
    var currentStep by remember { mutableIntStateOf(1) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    LaunchedEffect(recoverState.success) {
        if (recoverState.success) {
            currentStep = 2
            authViewModel.clearRecoverResult()
        }
    }

    AppBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentStep == 1) {
                Image(painter = painterResource(id = R.drawable.recovery), "...", Modifier.size(150.dp))
                Spacer(Modifier.height(24.dp))
                Text("Recuperar Contraseña", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("Ingresa tu correo...", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = recoverState.email,
                    onValueChange = authViewModel::onRecoverEmailChange,
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = recoverState.emailError != null,
                    supportingText = { recoverState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )

                recoverState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }

                Spacer(Modifier.height(24.dp))

                Button(onClick = authViewModel::submitRecover, enabled = !recoverState.isLoading) {
                    if (recoverState.isLoading) CircularProgressIndicator() else Text("Enviar Código")
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = { mainViewModel.navigateBack() }) {
                    Text("Cancelar")
                }

            } else {
                Image(painter = painterResource(id = R.drawable.envio_correo), "...", Modifier.size(120.dp))
                Spacer(Modifier.height(24.dp))
                Text("Restablecer Contraseña", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text("Hemos enviado un código a ${recoverState.email}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = resetState.code,
                    onValueChange = authViewModel::onResetCodeChange,
                    label = { Text("Código de 6 dígitos") }
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = resetState.newPassword,
                    onValueChange = authViewModel::onResetPassChange,
                    label = { Text("Nueva Contraseña") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    isError = resetState.passError != null,
                    supportingText = { resetState.passError?.let { Text(it, color = Color.Red) } }
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = resetState.confirmPassword,
                    onValueChange = authViewModel::onResetConfirmChange,
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) { Icon(if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    isError = resetState.confirmError != null,
                    supportingText = { resetState.confirmError?.let { Text(it, color = Color.Red) } }
                )

                resetState.error?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }
                Spacer(Modifier.height(24.dp))

                Button(onClick = authViewModel::submitReset, enabled = !resetState.isLoading) {
                    if (resetState.isLoading) CircularProgressIndicator() else Text("Cambiar Contraseña")
                }
                
                Spacer(Modifier.height(8.dp))

                TextButton(onClick = { currentStep = 1; authViewModel.clearResetState() }) {
                    Text("Volver")
                }
            }
        }
    }
}
