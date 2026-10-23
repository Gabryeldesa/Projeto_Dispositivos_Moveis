package com.example.dracma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Anuncio
import model.Categoria

// Tela 5 - Serviços da categoria: lista os anúncios de uma categoria
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicosCategoriaScreen(
    categoria: Categoria,
    onVoltar: () -> Unit,
    onPerfil: (Anuncio) -> Unit,
    onContratar: (Anuncio) -> Unit
) {
    var busca by remember { mutableStateOf("") }
    val anuncios = Repositorio.buscarAnuncios(busca, categoria)

    Scaffold(
        containerColor = FundoEscuro,
        topBar = {
            TopAppBar(
                title = { Text(categoria.titulo, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FundoEscuro)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TextField(
                value = busca,
                onValueChange = { busca = it },
                singleLine = true,
                placeholder = { Text("Buscar em ${categoria.titulo}") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (anuncios.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum serviço encontrado.", color = Color.White.copy(alpha = 0.7f))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(anuncios, key = { it.id }) { anuncio ->
                        CardAnuncio(anuncio, onPerfil, onContratar)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardAnuncio(
    anuncio: Anuncio,
    onPerfil: (Anuncio) -> Unit,
    onContratar: (Anuncio) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FundoCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarPadrao(44.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        anuncio.servico.titulo,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(anuncio.prestador.nome, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
                NotaAnuncio(anuncio.notaMedia())
            }
            if (anuncio.descricao.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(anuncio.descricao, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    formatarReais(anuncio.servico.valor),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = { onPerfil(anuncio) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) { Text("Perfil") }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onContratar(anuncio) },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxoBotao)
                ) { Text("Contratar") }
            }
        }
    }
}
