package com.example.dracma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Anuncio
import model.Categoria
import model.Prestador

// Tela 6 - Perfil do prestador
// Mostra os dados do prestador escolhido e a lista de serviços que ele anuncia.
// Recebe só o id: os dados vêm do Repositorio, igual às outras telas do app.
@Composable
fun PerfilPrestadorScreen(
    prestadorId: Int,
    onVoltar: () -> Unit,
    onConversar: (Int) -> Unit,
    onContratar: (Int) -> Unit
) {
    // Elvis: se o prestador não existir, mostra um aviso em vez de quebrar o app
    val prestador = Repositorio.buscarPrestador(prestadorId)
        ?: return PrestadorNaoEncontrado(onVoltar)

    val anuncios = Repositorio.anunciosDoPrestador(prestadorId)

    // Nota do prestador = média das notas dos anúncios dele.
    // Anúncios sem nenhuma avaliação (nota 0.0) ficam de fora da conta.
    val avaliados = anuncios.filter { it.notaMedia() > 0.0 }
    val notaGeral = if (avaliados.isEmpty()) 0.0 else avaliados.map { it.notaMedia() }.average()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
    ) {
        CabecalhoPerfil(onVoltar)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Topo: foto, nome da empresa, áreas de atuação e nota
            item {
                IdentificacaoPrestador(prestador, notaGeral)
                Spacer(Modifier.height(20.dp))
                BotaoConversar { onConversar(prestador.id) }
                Spacer(Modifier.height(20.dp))
            }

            // Descrição que o prestador escreveu no cadastro
            if (prestador.descricao.isNotBlank()) {
                item {
                    CardPerfil(titulo = "Sobre") {
                        Text(
                            prestador.descricao,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                CardPerfil(titulo = "Contato") {
                    LinhaComIcone(Icons.Filled.Phone, prestador.telefone)
                    Spacer(Modifier.height(8.dp))
                    LinhaComIcone(Icons.Filled.Email, prestador.email)
                    Spacer(Modifier.height(8.dp))
                    LinhaComIcone(
                        Icons.Filled.LocationOn,
                        "${prestador.endereco.bairro}, " +
                                "${prestador.endereco.cidade} - ${prestador.endereco.estado}"
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text(
                    "Serviços oferecidos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))
            }

            if (anuncios.isEmpty()) {
                item {
                    Text(
                        "Este prestador ainda não publicou nenhum serviço.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            } else {
                items(anuncios) { anuncio ->
                    CardServico(anuncio) { onContratar(anuncio.id) }
                    Spacer(Modifier.height(10.dp))
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ---------- Pedaços da tela ----------

// Barra de cima: seta de voltar + título
@Composable
private fun CabecalhoPerfil(onVoltar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
            "Perfil do prestador",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// Foto, nome da empresa, áreas de atuação e nota média
@Composable
private fun IdentificacaoPrestador(prestador: Prestador, notaGeral: Double) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarPadrao(tamanho = 110.dp)
        Spacer(Modifier.height(12.dp))
        Text(
            prestador.nome,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            textoDasAreas(prestador),
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 14.sp
        )
        Spacer(Modifier.height(10.dp))
        NotaAnuncio(notaGeral, cor = Color.White)
    }
}

// Botão roxo que leva para a tela de chat (tela 9)
@Composable
private fun BotaoConversar(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = RoxoBotao),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Conversar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// Caixa escura com título, usada nos blocos "Sobre" e "Contato".
// O parâmetro "conteudo" é uma função composable: quem chama decide o que vai dentro.
@Composable
private fun CardPerfil(titulo: String, conteudo: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FundoCard)
            .padding(16.dp)
    ) {
        Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(10.dp))
        conteudo()
    }
}

// Linha de "ícone + texto" usada no bloco de contato
@Composable
private fun LinhaComIcone(icone: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = RoxoBorda,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(texto, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
    }
}

// Card de um serviço anunciado. Clicar leva para a tela de contratação (tela 7).
@Composable
private fun CardServico(anuncio: Anuncio, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FundoCard)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(RoxoBotao),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconeDaCategoria(anuncio.servico.categoria),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                anuncio.servico.titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                anuncio.servico.descricaoCategoria(),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(4.dp))
            NotaAnuncio(anuncio.notaMedia())
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                formatarReais(anuncio.servico.valor),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(4.dp))
            // Cashback: mostra quantas DracCoins o cliente ganha nesse serviço
            Row(verticalAlignment = Alignment.CenterVertically) {
                MoedaDrac(tamanho = 18.dp)
                Spacer(Modifier.width(4.dp))
                Text(
                    formatarCoins(anuncio.servico.calcularCashback()),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Aviso para o caso raro de o id não corresponder a nenhum prestador
@Composable
private fun PrestadorNaoEncontrado(onVoltar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Prestador não encontrado", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onVoltar,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = RoxoBotao)
        ) { Text("Voltar") }
    }
}

// Monta o texto das áreas de atuação.
// Em "Outros" o prestador escreve a área dele no cadastro, então usamos esse texto.
private fun textoDasAreas(prestador: Prestador): String {
    return prestador.categorias.joinToString(" • ") { categoria ->
        if (categoria == Categoria.OUTROS && prestador.areaOutros.isNotBlank()) {
            prestador.areaOutros
        } else {
            categoria.titulo
        }
    }
}
