package com.example.dracma.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FundoEscuro = Color(0xFF42285A)
val FundoCard = Color(0xFF35204A)
val RoxoBotao = Color(0xFF7419BE)
val RoxoBorda = Color(0xFF7A2BD0)

@Composable
fun DracmaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = RoxoBotao,
            onPrimary = Color.White,
            background = FundoEscuro,
            onBackground = Color.White
        ),
        content = content
    )
}