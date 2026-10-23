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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Repositorio
import model.Categoria
import model.Endereco

@Composable
fun CadastroPrestadorScreen(
    onCadastrado: () -> Unit,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current

    var nomeEmpresa by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nomeUsuario by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var areaOutros by remember { mutableStateOf("") }
    var cpfCnpj by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var rua by remember { mutableStateOf("") }
    var complemento by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var bairro by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmacao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    // Lista de áreas marcadas nos checkboxes
    val selecionadas = remember { mutableStateListOf<Categoria>() }

    fun alternar(categoria: Categoria) {
        if (selecionadas.contains(categoria)) {
            selecionadas.remove(categoria)
        } else {
            selecionadas.add(categoria)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto (espaço reservado)
        AvatarPadrao(110.dp)
        Spacer(Modifier.height(4.dp))
        EscolherFoto("Escolher fotos da empresa")
        Spacer(Modifier.height(16.dp))

        CampoCadastro("Nome da empresa", nomeEmpresa, { nomeEmpresa = it })
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Email", email, { email = it }, tipoTeclado = KeyboardType.Email)
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Nome de usuario", nomeUsuario, { nomeUsuario = it })
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Descrição", descricao, { descricao = it })
        Spacer(Modifier.height(16.dp))

        // Áreas de atuação (checkboxes em duas colunas)
        Text(
            "Qual a sua área de atuação?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Categoria.entries.chunked(2).forEach { linha ->
            Row(modifier = Modifier.fillMaxWidth()) {
                linha.forEach { categoria ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { alternar(categoria) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selecionadas.contains(categoria),
                            onCheckedChange = { alternar(categoria) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = RoxoBotao,
                                uncheckedColor = Color.White,
                                checkmarkColor = Color.White
                            )
                        )
                        Text(categoria.titulo, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        // NOVO: se marcou "Outros", pede para descrever a área de atuação
        if (selecionadas.contains(Categoria.OUTROS)) {
            Spacer(Modifier.height(8.dp))
            CampoCadastro("Descreva sua área de atuação", areaOutros, { areaOutros = it })
        }
        Spacer(Modifier.height(8.dp))

        CampoCadastro("CPF/CNPJ", cpfCnpj, { cpfCnpj = it }, tipoTeclado = KeyboardType.Number)
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Telefone", telefone, { telefone = it }, tipoTeclado = KeyboardType.Phone)
        Spacer(Modifier.height(8.dp))
        CampoCadastro("CEP", cep, { cep = it }, tipoTeclado = KeyboardType.Number)
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Rua", rua, { rua = it })
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Complemento (opcional)", complemento, { complemento = it })
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CampoCadastro(
                "Nº", numero, { numero = it },
                modifier = Modifier.weight(1f),
                tipoTeclado = KeyboardType.Number
            )
            Spacer(Modifier.width(16.dp))
            CampoCadastro("Bairro", bairro, { bairro = it }, modifier = Modifier.weight(2f))
        }
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CampoCadastro("Cidade", cidade, { cidade = it }, modifier = Modifier.weight(2f))
            Spacer(Modifier.width(16.dp))
            CampoCadastro("Estado (UF)", estado, { estado = it }, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))

        CampoCadastro("Senha", senha, { senha = it }, senha = true)
        Spacer(Modifier.height(8.dp))
        CampoCadastro("Confirme sua senha", confirmacao, { confirmacao = it }, senha = true)

        erro?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (descricao.isBlank()) {
                    erro = "Escreva uma breve descrição da empresa"
                } else if (cep.isBlank() || rua.isBlank() || numero.isBlank() ||
                    bairro.isBlank() || cidade.isBlank() || estado.isBlank()
                ) {
                    erro = "Preencha o endereço completo"
                } else {
                    val resultado = Repositorio.cadastrarPrestador(
                        nomeEmpresa = nomeEmpresa.trim(),
                        email = email.trim(),
                        nomeUsuario = nomeUsuario.trim(),
                        telefone = telefone.trim(),
                        endereco = Endereco(
                            cep.trim(), rua.trim(), complemento.trim(), numero.trim(),
                            bairro.trim(), cidade.trim(), estado.trim().uppercase()
                        ),
                        cpfCnpj = cpfCnpj.trim(),
                        descricao = descricao.trim(),
                        categorias = selecionadas.toList(),
                        senha = senha,
                        confirmacao = confirmacao,
                        areaOutros = if (selecionadas.contains(Categoria.OUTROS)) areaOutros.trim() else ""
                    )
                    erro = resultado
                    if (resultado == null) {
                        Toast.makeText(context, "Cadastro realizado! Faça login.", Toast.LENGTH_LONG).show()
                        onCadastrado()
                    }
                }
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(52.dp)
        ) {
            Text("CONFIRMAR", color = RoxoBotao, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        TextButton(onClick = onVoltar) {
            Text("Voltar", color = Color.White)
        }
    }
}