package com.example.dracma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio

@Composable
fun TelaProvisoria(titulo: String, onVoltar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo, color = Color.White, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onVoltar) { Text("Voltar") }
    }
}

@Composable
fun HomeProvisoria(onSair: () -> Unit) {
    val usuario = Repositorio.usuarioLogado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Olá, ${usuario?.nome ?: "visitante"}!", color = Color.White, fontSize = 22.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Anúncios no feed: ${Repositorio.buscarAnuncios().size}",
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            Repositorio.sair()
            onSair()
        }) { Text("Sair") }
    }
}