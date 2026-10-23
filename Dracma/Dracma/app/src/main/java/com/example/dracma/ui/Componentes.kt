package com.example.dracma.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Moeda Drac: círculo com "$" (usada no chip de saldo)
@Composable
fun MoedaDrac(tamanho: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "\$",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (tamanho.value * 0.55f).sp
        )
    }
}

// Foto padrão: círculo com o desenho de uma pessoa
@Composable
fun AvatarPadrao(tamanho: Dp) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .border(3.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(tamanho * 0.6f)
        )
    }
}

// "Escolher foto" com ícone de câmera
@Composable
fun EscolherFoto(rotulo: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(rotulo, color = Color.White, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

// Nota do anúncio: estrela + número, ou "Sem avaliações" quando ainda não tem nota
@Composable
fun NotaAnuncio(nota: Double, cor: Color = Color.White.copy(alpha = 0.7f)) {
    if (nota == 0.0) {
        Text("Sem avaliações", color = cor, fontSize = 12.sp)
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(formatarNota(nota), color = cor, fontSize = 12.sp)
        }
    }
}