package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.AuthViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val estado by authViewModel.login.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    AppBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    shadow = Shadow(color = Color.Black.copy(alpha = 0.5f), blurRadius = 4f)
                ),
                color = AmarilloAji
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = estado.correo,
                onValueChange = authViewModel::onLoginEmailChange,
                label = { Text("Email", color = MoradoAji) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = estado.correoError != null,
                supportingText = { estado.correoError?.let { Text(it, color = Color.Red) } }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = estado.clave,
                onValueChange = authViewModel::onLoginPassChange,
                label = { Text("Contraseña", color = MoradoAji) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            estado.errorMsg?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = authViewModel::submitLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = estado.canSubmit && !estado.isSubmitting
            ) {
                if (estado.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Entrar")
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { mainViewModel.navigate(Screen.Registro.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { mainViewModel.navigate(Screen.PasswordRecovery.route) }) {
                Text("Recuperar contraseña")
            }
        }
    }
}
