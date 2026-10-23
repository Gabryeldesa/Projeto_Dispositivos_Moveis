package com.example.dracma.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector
import model.Categoria
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val brasil: Locale = Locale.forLanguageTag("pt-BR")

fun formatarReais(valor: Double): String {
    return String.format(brasil, "R$ %.2f", valor)
}

fun formatarCoins(valor: Double): String {
    return String.format(brasil, "%.1f", valor)
}

fun formatarNota(nota: Double): String {
    return String.format(brasil, "%.1f", nota)
}

// Hora da mensagem no chat, no formato 14:35.
// Usamos SimpleDateFormat porque o java.time só funciona do Android 8 em diante
// e o app aceita a partir do Android 7 (minSdk 24).
fun formatarHora(instante: Long): String {
    return SimpleDateFormat("HH:mm", brasil).format(Date(instante))
}

fun iconeDaCategoria(categoria: Categoria): ImageVector {
    return when (categoria) {
        Categoria.ENCANACAO -> Icons.Filled.Plumbing
        Categoria.ELETRICISTA -> Icons.Filled.ElectricBolt
        Categoria.ESTETICA -> Icons.Filled.Spa
        Categoria.DOMESTICOS -> Icons.Filled.CleaningServices
        Categoria.CONSTRUCAO -> Icons.Filled.Construction
        Categoria.OUTROS -> Icons.Filled.MiscellaneousServices
    }
}