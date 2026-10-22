package com.example.dracma.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dracma.R
import data.Repositorio

@Composable
fun LoginScreen(onEntrar: () -> Unit, onCadastro: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    val camposCores = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        // Cabeçalho escuro com a logo do Dracma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(bottomEnd = 140.dp))
                .background(FundoEscuro),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_dracma),
                contentDescription = "Logo do Dracma",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = RoxoBotao)
                },
                singleLine = true,
                shape = CircleShape,
                colors = camposCores,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = { Text("Senha") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = RoxoBotao)
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = CircleShape,
                colors = camposCores,
                modifier = Modifier.fillMaxWidth()
            )

            erro?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (email.isBlank() || senha.isBlank()) {
                        erro = "Preencha e-mail e senha"
                    } else {
                        val usuario = Repositorio.login(email.trim(), senha)
                        if (usuario == null) {
                            erro = "E-mail ou senha incorretos"
                        } else {
                            erro = null
                            onEntrar()
                        }
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = RoxoBotao),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Fazer Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Ainda não tem uma conta?", color = Color.Gray, fontSize = 13.sp)
                TextButton(onClick = onCadastro) {
                    Text("Cadastre-se aqui", color = RoxoBotao, fontSize = 13.sp)
                }
            }
        }
    }
}