package com.example.dracma.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio

private val CorEstrela = Color(0xFFFFB300)
private val TextosDaNota = listOf("Muito ruim", "Ruim", "Regular", "Bom", "Excelente")

// Tela "Avaliar prestador": o cliente escolhe de 1 a 5 estrelas e envia.
// Recebe o id do anúncio (serviço contratado) que está sendo avaliado.
@Composable
fun AvaliarPrestadorScreen(
    anuncioId: Int,
    onVoltar: () -> Unit,
    onConcluido: () -> Unit   // chamada depois que a avaliação é salva
) {
    val context = LocalContext.current

    // Procura entre os pedidos do cliente logado o que tem esse anúncio
    val pedido = Repositorio.minhasContratacoes().firstOrNull { it.anuncio.id == anuncioId }

    // Nota escolhida na tela (0 = ainda não escolheu). O Compose redesenha ao mudar.
    var nota by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CabecalhoAvaliar(onVoltar)

        if (pedido == null) {
            Text(
                "Pedido não encontrado",
                color = Color.White,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                AvatarPadrao(tamanho = 110.dp)
                Spacer(Modifier.height(12.dp))
                Text(
                    pedido.anuncio.prestador.nome,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    pedido.anuncio.servico.titulo,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))
                Text(
                    "Como foi o serviço?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(12.dp))

                EstrelasDaNota(nota = nota, aoEscolher = { nota = it })

                Spacer(Modifier.height(8.dp))
                Text(
                    if (nota == 0) "Toque nas estrelas para avaliar" else TextosDaNota[nota - 1],
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        // O Repositorio valida e devolve a mensagem de erro (ou null se deu certo)
                        val erro = Repositorio.avaliarAnuncio(anuncioId, nota.toDouble())
                        if (erro == null) {
                            Toast.makeText(context, "Obrigado pela avaliação!", Toast.LENGTH_SHORT).show()
                            onConcluido()
                        } else {
                            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = nota > 0,   // só libera depois de escolher uma nota
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color.White.copy(alpha = 0.15f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Enviar avaliação")
                }
            }
        }
    }
}

// ---------- Pedaços da tela ----------

@Composable
private fun CabecalhoAvaliar(onVoltar: () -> Unit) {
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
            "Avaliar prestador",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// Cinco estrelas clicáveis: as até a nota escolhida ficam preenchidas
@Composable
private fun EstrelasDaNota(nota: Int, aoEscolher: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (posicao in 1..5) {
            val cheia = posicao <= nota
            Icon(
                imageVector = if (cheia) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "$posicao estrela(s)",
                tint = CorEstrela,
                modifier = Modifier
                    .size(44.dp)
                    .clickable { aoEscolher(posicao) }
            )
        }
    }
}
