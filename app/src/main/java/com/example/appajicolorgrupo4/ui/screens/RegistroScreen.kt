package com.example.appajicolorgrupo4.ui.screens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.AuthViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel

@Composable
fun RegistroScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    val registerState by authViewModel.register.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    registerState.errorMsg?.let {
        AlertDialog(
            onDismissRequest = { authViewModel.clearRegisterResult() },
            title = { Text("Error de Registro") },
            text = { Text(it) },
            confirmButton = { Button(onClick = { authViewModel.clearRegisterResult() }) { Text("OK") } }
        )
    }

    AppBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge, color = AmarilloAji)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = registerState.nombre,
                onValueChange = authViewModel::onNameChange,
                label = { Text("Nombre Completo", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth(),
                isError = registerState.nombreError != null,
                supportingText = { registerState.nombreError?.let { Text(it, color = Color.Red) } }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = registerState.correo,
                onValueChange = authViewModel::onRegisterEmailChange,
                label = { Text("Correo Electrónico", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = registerState.correoError != null,
                supportingText = { registerState.correoError?.let { Text(it, color = Color.Red) } }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = registerState.telefono,
                onValueChange = authViewModel::onTelefonoChange,
                label = { Text("Teléfono", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = registerState.direccion,
                onValueChange = authViewModel::onDireccionChange,
                label = { Text("Dirección", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = registerState.clave,
                onValueChange = authViewModel::onRegisterPassChange,
                label = { Text("Contraseña", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                isError = registerState.claveError != null,
                supportingText = { registerState.claveError?.let { Text(it, color = Color.Red) } }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = registerState.confirm,
                onValueChange = authViewModel::onConfirmChange,
                label = { Text("Confirmar Contraseña", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) { Icon(if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                isError = registerState.confirmError != null,
                supportingText = { registerState.confirmError?.let { Text(it, color = Color.Red) } }
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.submitRegister() },
                modifier = Modifier.fillMaxWidth(),
                enabled = registerState.canSubmit && !registerState.isSubmitting
            ) {
                if (registerState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Registrarse")
                }
            }
            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { mainViewModel.navigate(Screen.Login.route) }) {
                Text("¿Ya tienes cuenta? Inicia Sesión", color = AmarilloAji)
            }
        }
    }
}
