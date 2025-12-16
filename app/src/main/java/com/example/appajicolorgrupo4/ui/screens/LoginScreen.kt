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
    authViewModel: AuthViewModel, // Recibe el ViewModel de autenticación
    mainViewModel: MainViewModel  // Recibe el ViewModel de navegación
) {
    val estado by authViewModel.login.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    // La navegación ahora se maneja completamente dentro de AuthViewModel.
    // Ya no se necesita el LaunchedEffect aquí.

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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

            // Campo Email
            OutlinedTextField(
                value = estado.correo,
                onValueChange = authViewModel::onLoginEmailChange,
                label = { Text("Email", color = MoradoAji, fontWeight = FontWeight.Bold) },
                placeholder = { Text("Ingrese su email", color = MoradoAji.copy(alpha = 0.7f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmarilloAji,
                    unfocusedBorderColor = AmarilloAji,
                    focusedLabelColor = MoradoAji,
                    unfocusedLabelColor = MoradoAji,
                    cursorColor = AmarilloAji,
                    focusedTextColor = MoradoAji,
                    unfocusedTextColor = MoradoAji,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                isError = estado.correoError != null,
                supportingText = {
                    estado.correoError?.let {
                        Text(it, color = AmarilloAji)
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            // Campo Password
            OutlinedTextField(
                value = estado.clave,
                onValueChange = authViewModel::onLoginPassChange,
                label = { Text("Contraseña", color = MoradoAji, fontWeight = FontWeight.Bold) },
                placeholder = { Text("Ingrese su contraseña", color = MoradoAji.copy(alpha = 0.7f)) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = AmarilloAji
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmarilloAji,
                    unfocusedBorderColor = AmarilloAji,
                    focusedLabelColor = MoradoAji,
                    unfocusedLabelColor = MoradoAji,
                    cursorColor = AmarilloAji,
                    focusedTextColor = MoradoAji,
                    unfocusedTextColor = MoradoAji,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))

            // Mostrar error de login
            estado.errorMsg?.let { error ->
                Text(
                    text = error,
                    color = AmarilloAji,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.3f), blurRadius = 2f)
                    )
                )
                Spacer(Modifier.height(8.dp))
            }

            // Botón Entrar
            Button(
                onClick = authViewModel::submitLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = estado.canSubmit && !estado.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji,
                    disabledContainerColor = AmarilloAji.copy(alpha = 0.5f),
                    disabledContentColor = MoradoAji.copy(alpha = 0.5f)
                ),
                border = BorderStroke(2.dp, MoradoAji)
            ) {
                if (estado.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MoradoAji,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...", fontWeight = FontWeight.Bold)
                } else {
                    Text("Entrar", fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón Ir a Registro
            OutlinedButton(
                onClick = { mainViewModel.navigate(Screen.Registro) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji
                ),
                border = BorderStroke(1.dp, MoradoAji)
            ) {
                Text("Crear cuenta", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            // Botón Recuperar Contraseña
            TextButton(
                onClick = { mainViewModel.navigate(Screen.PasswordRecovery) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AmarilloAji
                )
            ) {
                Text("Recuperar contraseña", fontWeight = FontWeight.Bold)
            }
        }
    }
}
