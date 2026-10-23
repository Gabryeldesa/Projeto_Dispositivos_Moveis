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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Contratacao

// Tela "Meus pedidos": lista os serviços que o cliente logado já contratou.
// Cada pedido tem um botão "Avaliar prestador" que abre a tela de avaliação.
@Composable
fun MeusPedidosScreen(
    onVoltar: () -> Unit,
    onAvaliar: (Int) -> Unit   // recebe o id do anúncio que será avaliado
) {
    // reversed(): os pedidos mais recentes aparecem primeiro
    val pedidos = Repositorio.minhasContratacoes().reversed()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CabecalhoPedidos(onVoltar)

        if (pedidos.isEmpty()) {
            SemPedidos()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos) { pedido ->
                    CardPedido(
                        pedido = pedido,
                        onAvaliar = { onAvaliar(pedido.anuncio.id) }
                    )
                }
            }
        }
    }
}

// ---------- Pedaços da tela ----------

@Composable
private fun CabecalhoPedidos(onVoltar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FundoCard)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVoltar) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }
        Text(
            "Meus pedidos",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// Um pedido: ícone da categoria, serviço, prestador, valor, cashback, nota e botão de avaliar
@Composable
private fun CardPedido(pedido: Contratacao, onAvaliar: () -> Unit) {
    val anuncio = pedido.anuncio

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FundoCard)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconeDaCategoria(anuncio.servico.categoria),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    anuncio.servico.titulo,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    anuncio.prestador.nome,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Valor pago: ${formatarReais(pedido.valorPago)}",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            NotaAnuncio(anuncio.notaMedia())
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Cashback: ${formatarCoins(pedido.cashback)} Drac Coins",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(14.dp))

        Button(
            onClick = onAvaliar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Avaliar prestador")
        }
    }
}

@Composable
private fun SemPedidos() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Você ainda não tem pedidos",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Contrate um serviço no perfil de um prestador e ele aparece aqui.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}
