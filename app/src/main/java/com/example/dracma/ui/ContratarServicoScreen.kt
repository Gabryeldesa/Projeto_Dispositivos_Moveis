package com.example.dracma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Anuncio

// Tela 7 - Contratar serviço: resumo do anúncio, dados do agendamento e confirmação
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContratarServicoScreen(
    anuncio: Anuncio,
    onVoltar: () -> Unit,
    onContratado: () -> Unit
) {
    var data by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var sucesso by remember { mutableStateOf(false) }

    val servico = anuncio.servico
    val cashback = servico.calcularCashback()

    Scaffold(
        containerColor = FundoEscuro,
        topBar = {
            TopAppBar(
                title = { Text("Contratar serviço", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Resumo do anúncio
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(FundoCard, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(servico.titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(anuncio.prestador.nome, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                NotaAnuncio(anuncio.notaMedia())
                Spacer(Modifier.height(8.dp))
                Text(servico.descricaoCategoria(), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                CampoMascara("Data", data, { data = it }, "##/##/####", "dd/mm/aaaa", Modifier.weight(1f))
                Spacer(Modifier.padding(6.dp))
                CampoMascara("Horário", horario, { horario = it }, "##:##", "hh:mm", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            CampoCadastro("Observações (opcional)", observacoes, { observacoes = it })

            Spacer(Modifier.height(24.dp))
            LinhaValor("Valor do serviço", formatarReais(servico.valor))
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Cashback", color = Color.White, modifier = Modifier.weight(1f))
                MoedaDrac(20.dp)
                Text(" +${formatarCoins(cashback)}", color = Color.White, fontWeight = FontWeight.Bold)
            }

            erro?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (!dataValida(data)) {
                        erro = "Informe uma data válida (dd/mm/aaaa)."
                    } else if (!horaValida(horario)) {
                        erro = "Informe um horário válido (hh:mm)."
                    } else {
                        // O Repositório devolve null quando deu certo, ou o texto do erro
                        val resultado = Repositorio.contratar(anuncio.id)
                        erro = resultado
                        sucesso = resultado == null
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoxoBotao),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text("Confirmar contratação", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (sucesso) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Serviço contratado!") },
            text = {
                Text(
                    "Agendado para ${aplicarMascara(data, "##/##/####")} às " +
                            "${aplicarMascara(horario, "##:##")}.\n" +
                            "Você ganhou ${formatarCoins(cashback)} Drac Coins de cashback."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    sucesso = false
                    onContratado()
                }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun LinhaValor(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(rotulo, color = Color.White, modifier = Modifier.weight(1f))
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold)
    }
}


// ---------- Campos com máscara (data e horário) ----------

// Guarda só os dígitos; a máscara é aplicada apenas na hora de mostrar
@Composable
private fun CampoMascara(
    rotulo: String,
    digitos: String,
    aoMudar: (String) -> Unit,
    mascara: String,
    dica: String,
    modifier: Modifier = Modifier
) {
    val maximo = mascara.count { it == '#' }
    Column(modifier) {
        Text(rotulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        TextField(
            value = digitos,
            onValueChange = { novo -> aoMudar(novo.filter { it.isDigit() }.take(maximo)) },
            singleLine = true,
            placeholder = { Text(dica, color = Color.White.copy(alpha = 0.4f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = MascaraVisual(mascara),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Coloca as barras / dois-pontos na exibição: "25122026" vira "25/12/2026"
private class MascaraVisual(private val mascara: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text
        val saida = aplicarMascara(digitos, mascara)

        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var posicao = 0
                var contados = 0
                for (c in mascara) {
                    if (contados >= offset) break
                    posicao++
                    if (c == '#') contados++
                }
                return posicao.coerceAtMost(saida.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val usados = mascara.take(offset).count { it == '#' }
                return usados.coerceAtMost(digitos.length)
            }
        }
        return TransformedText(AnnotatedString(saida), mapa)
    }
}

private fun aplicarMascara(digitos: String, mascara: String): String {
    val saida = StringBuilder()
    var i = 0
    for (c in mascara) {
        if (i >= digitos.length) break
        if (c == '#') {
            saida.append(digitos[i])
            i++
        } else {
            saida.append(c)
        }
    }
    return saida.toString()
}

// Recebem só os dígitos (ex.: "25122026" e "1430")
private fun dataValida(d: String): Boolean {
    if (d.length != 8) return false
    val dia = d.substring(0, 2).toInt()
    val mes = d.substring(2, 4).toInt()
    val ano = d.substring(4, 8).toInt()
    if (ano < 2000 || mes !in 1..12) return false
    val diasNoMes = when (mes) {
        2 -> if (ano % 4 == 0 && (ano % 100 != 0 || ano % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    return dia in 1..diasNoMes
}

private fun horaValida(h: String): Boolean {
    if (h.length != 4) return false
    return h.substring(0, 2).toInt() in 0..23 && h.substring(2, 4).toInt() in 0..59
}