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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Mensagem

// Tela 9 - Chat
// Conversa entre quem está logado e a outra pessoa (cliente <-> prestador).
// Recebe o id da conversa; as mensagens vêm do Repositorio, que salva tudo no JSON.
@Composable
fun ChatScreen(conversaId: Int, onVoltar: () -> Unit) {
    val conversa = Repositorio.buscarConversa(conversaId)
        ?: return ConversaNaoEncontrada(onVoltar)

    val logado = Repositorio.usuarioLogado
        ?: return ConversaNaoEncontrada(onVoltar)

    val outro = conversa.outroParticipante(logado.id)

    // Estado da tela: o que está escrito na caixa e a lista de mensagens.
    // "by remember { mutableStateOf(...) }" faz o Compose redesenhar quando muda.
    var texto by remember { mutableStateOf("") }
    var mensagens by remember { mutableStateOf(conversa.listaMensagens()) }
    var erro by remember { mutableStateOf<String?>(null) }

    val estadoLista = rememberLazyListState()

    // Sempre que chega mensagem nova, rola a lista até o fim.
    // LaunchedEffect roda o bloco quando a "chave" (o tamanho da lista) muda.
    LaunchedEffect(mensagens.size) {
        if (mensagens.isNotEmpty()) {
            estadoLista.animateScrollToItem(mensagens.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
            .imePadding()
    ) {
        CabecalhoChat(nome = outro.nome, onVoltar = onVoltar)

        // weight(1f) faz a lista ocupar todo o espaço que sobra,
        // deixando a caixa de escrever sempre colada embaixo
        Box(modifier = Modifier.weight(1f)) {
            if (mensagens.isEmpty()) {
                ConversaVazia(outro.nome)
            } else {
                LazyColumn(
                    state = estadoLista,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    items(mensagens) { mensagem ->
                        BalaoMensagem(
                            mensagem = mensagem,
                            minha = mensagem.autorId == logado.id
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        erro?.let {
            Text(
                it,
                color = Color(0xFFFF8A80),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        CaixaDeEnvio(
            texto = texto,
            aoMudar = {
                texto = it
                erro = null
            },
            aoEnviar = {
                val problema = Repositorio.enviarMensagem(conversaId, texto)
                if (problema != null) {
                    erro = problema
                } else {
                    texto = ""
                    erro = null
                    // Relê a lista do Repositorio para a tela se atualizar
                    mensagens = conversa.listaMensagens()
                }
            }
        )
    }
}

// ---------- Pedaços da tela ----------

// Barra de cima: voltar, foto pequena e nome de quem está do outro lado
@Composable
private fun CabecalhoChat(nome: String, onVoltar: () -> Unit) {
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
        AvatarPadrao(tamanho = 40.dp)
        Spacer(Modifier.width(12.dp))
        Text(
            nome,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}

// Balão de uma mensagem.
// "minha" decide o lado e a cor: minhas mensagens vão para a direita, em roxo.
@Composable
private fun BalaoMensagem(mensagem: Mensagem, minha: Boolean) {
    val formato = if (minha) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (minha) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(formato)
                .background(if (minha) RoxoBotao else FundoCard)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(mensagem.texto, color = Color.White, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                formatarHora(mensagem.instante),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// Caixa de escrever + botão de enviar, fixos embaixo
@Composable
private fun CaixaDeEnvio(texto: String, aoMudar: (String) -> Unit, aoEnviar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FundoCard)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = texto,
            onValueChange = aoMudar,
            placeholder = {
                Text("Digite uma mensagem", color = Color.White.copy(alpha = 0.5f))
            },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FundoEscuro,
                unfocusedContainerColor = FundoEscuro,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                // Tira a linha de baixo que o TextField desenha por padrão
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = aoEnviar,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(RoxoBotao)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// Mostrado quando a conversa ainda não tem nenhuma mensagem
@Composable
private fun ConversaVazia(nome: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Nenhuma mensagem ainda",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Mande a primeira mensagem para $nome e combine os detalhes do serviço.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}

// Aviso para quando a conversa não existe ou ninguém está logado
@Composable
private fun ConversaNaoEncontrada(onVoltar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Conversa não encontrada", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onVoltar,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = RoxoBotao)
        ) { Text("Voltar") }
    }
}
