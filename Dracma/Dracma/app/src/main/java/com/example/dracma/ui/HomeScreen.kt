package com.example.dracma.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Anuncio
import model.Categoria
import model.Cliente

@Composable
fun HomeScreen(
    onSair: () -> Unit,
    onAnuncio: (Int) -> Unit,
    onCategoria: (Categoria) -> Unit
) {
    var aba by remember { mutableStateOf(0) }
    var busca by remember { mutableStateOf("") }

    // Só o cliente tem saldo; para os outros usuários mostra 0
    val saldo = (Repositorio.usuarioLogado as? Cliente)?.consultarSaldo() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        BarraTopo(saldo = saldo, onSair = onSair)
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(RoxoBorda)
        )
        Abas(selecionada = aba, aoSelecionar = { aba = it })
        BarraBusca(texto = busca, aoMudar = { busca = it })

        if (aba == 0) {
            val anuncios = Repositorio.buscarAnuncios(texto = busca)
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (busca.isBlank()) {
                    item { Banner() }
                }
                if (anuncios.isEmpty()) {
                    item {
                        Text(
                            "Nenhum serviço encontrado",
                            color = Color.White,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
                items(anuncios) { anuncio ->
                    ItemAnuncio(anuncio = anuncio, onClick = { onAnuncio(anuncio.id) })
                }
            }
        } else {
            val categorias = Categoria.entries.filter { categoria ->
                busca.isBlank() ||
                        categoria.titulo.contains(busca, ignoreCase = true) ||
                        categoria.servicos.any { it.contains(busca, ignoreCase = true) }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (categorias.isEmpty()) {
                    item {
                        Text(
                            "Nenhuma categoria encontrada",
                            color = Color.White,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
                items(categorias) { categoria ->
                    CardCategoria(categoria = categoria, onClick = { onCategoria(categoria) })
                }
            }
        }
    }
}

@Composable
private fun BarraTopo(saldo: Double, onSair: () -> Unit) {
    val context = LocalContext.current
    var menuAberto by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chip de saldo
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoedaDrac(20.dp)
            Spacer(Modifier.width(8.dp))
            Text(formatarCoins(saldo), color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.weight(1f))

        IconeTopo(Icons.Outlined.ChatBubbleOutline, "Chat") {
            Toast.makeText(context, "Chat em breve", Toast.LENGTH_SHORT).show()
        }
        IconeTopo(Icons.Outlined.Settings, "Configurações") {
            Toast.makeText(context, "Configurações em breve", Toast.LENGTH_SHORT).show()
        }
        Box {
            IconeTopo(Icons.Outlined.AccountCircle, "Perfil") { menuAberto = true }
            DropdownMenu(expanded = menuAberto, onDismissRequest = { menuAberto = false }) {
                DropdownMenuItem(
                    text = { Text("Sair") },
                    onClick = {
                        menuAberto = false
                        Repositorio.sair()
                        onSair()
                    }
                )
            }
        }
    }
}

@Composable
private fun IconeTopo(icone: ImageVector, descricao: String, onClick: () -> Unit) {
    Icon(
        imageVector = icone,
        contentDescription = descricao,
        tint = Color.White,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .size(28.dp)
    )
}

@Composable
private fun Abas(selecionada: Int, aoSelecionar: (Int) -> Unit) {
    val titulos = listOf("PARA VOCÊ", "TODOS OS SERVIÇOS")

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        titulos.forEachIndexed { indice, titulo ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    titulo,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (indice == selecionada) Color.White.copy(alpha = 0.25f)
                            else Color.Transparent
                        )
                        .clickable { aoSelecionar(indice) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(RoxoBorda)
    )
}

// Sem "private": também é usada na tela de categoria
@Composable
fun BarraBusca(texto: String, aoMudar: (String) -> Unit) {
    val fundo = Color.White.copy(alpha = 0.25f)
    TextField(
        value = texto,
        onValueChange = aoMudar,
        singleLine = true,
        placeholder = {
            Text("BUSCAR", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
        },
        trailingIcon = {
            Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White)
        },
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fundo,
            unfocusedContainerColor = fundo,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun Banner() {
    val formato = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(formato)
            .border(2.dp, RoxoBorda, formato)
            .background(Brush.horizontalGradient(listOf(Color(0xFF1B1030), RoxoBotao)))
            .padding(20.dp)
    ) {
        Column {
            Text(
                "CASHBACK EM DRAC COINS",
                color = Color(0xFFFF7A00),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Contrate serviços e receba de volta até 8% em Drac Coins.",
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}

// Sem "private": também é usada na tela de categoria
@Composable
fun ItemAnuncio(anuncio: Anuncio, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FundoCard)
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarPadrao(72.dp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    anuncio.prestador.nome,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(anuncio.servico.titulo, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                Text(
                    "Área: ${anuncio.servico.categoria.titulo}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                NotaAnuncio(anuncio.notaMedia())
            }
            Text(
                formatarReais(anuncio.servico.valor),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(FundoEscuro)
        )
    }
}

@Composable
private fun CardCategoria(categoria: Categoria, onClick: () -> Unit) {
    val formato = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(formato)
            .border(2.dp, RoxoBorda, formato)
            .background(FundoCard)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconeDaCategoria(categoria),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                categoria.titulo.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            categoria.servicos.forEach { servico ->
                Text("› $servico", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
            }
        }
    }
}