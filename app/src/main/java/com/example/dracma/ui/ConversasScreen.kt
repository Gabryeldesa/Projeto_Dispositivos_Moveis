package com.example.dracma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Conversa

// Lista de conversas (entrada para a tela 9 - Chat)
// Mostra com quem a pessoa logada já conversou, com a prévia da última mensagem.
// Funciona tanto para cliente quanto para prestador: o Repositorio devolve as
// conversas de quem está logado, e cada linha mostra a outra pessoa.
@Composable
fun ConversasScreen(onVoltar: () -> Unit, onAbrirConversa: (Int) -> Unit) {
    val logado = Repositorio.usuarioLogado
    val conversas = Repositorio.minhasConversas()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CabecalhoConversas(onVoltar)

        if (logado == null || conversas.isEmpty()) {
            SemConversas()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(conversas) { conversa ->
                    LinhaConversa(
                        conversa = conversa,
                        meuId = logado.id,
                        onClick = { onAbrirConversa(conversa.id) }
                    )
                }
            }
        }
    }
}

// ---------- Pedaços da tela ----------

@Composable
private fun CabecalhoConversas(onVoltar: () -> Unit) {
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
            "Conversas",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// Uma linha da lista: foto, nome da outra pessoa, prévia da última mensagem e hora
@Composable
private fun LinhaConversa(conversa: Conversa, meuId: Int, onClick: () -> Unit) {
    val outro = conversa.outroParticipante(meuId)
    val ultima = conversa.ultimaMensagem()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarPadrao(tamanho = 52.dp)
            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    outro.nome,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    // "Você: " na frente quando a última mensagem foi minha, igual ao WhatsApp
                    text = when {
                        ultima == null -> "Nenhuma mensagem ainda"
                        ultima.autorId == meuId -> "Você: ${ultima.texto}"
                        else -> ultima.texto
                    },
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (ultima != null) {
                Spacer(Modifier.width(10.dp))
                Text(
                    formatarHora(ultima.instante),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }

        // Risquinho separando as conversas
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.12f))
        )
    }
}

@Composable
private fun SemConversas() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Nenhuma conversa ainda",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Abra o perfil de um prestador e toque em Conversar para começar.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}
