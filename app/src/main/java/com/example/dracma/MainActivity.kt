package com.example.dracma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dracma.ui.CadastroClienteScreen
import com.example.dracma.ui.CadastroPrestadorScreen
import com.example.dracma.ui.DracmaTheme
import com.example.dracma.ui.HomeProvisoria
import com.example.dracma.ui.LoginScreen
import data.Repositorio

// Nomes das rotas em um só lugar (evita erro de digitação nas strings)
object Rotas {
    const val LOGIN = "login"
    const val CADASTRO_CLIENTE = "cadastro_cliente"
    const val CADASTRO_PRESTADOR = "cadastro_prestador"
    const val HOME = "home"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carrega os dados salvos (ou cria os iniciais na primeira vez)
        Repositorio.iniciar(applicationContext)

        setContent {
            DracmaTheme {
                DracmaApp()
            }
        }
    }
}

@Composable
fun DracmaApp() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Rotas.LOGIN) {
        composable(Rotas.LOGIN) {
            LoginScreen(
                onEntrar = {
                    nav.navigate(Rotas.HOME) { popUpTo(Rotas.LOGIN) { inclusive = true } }
                },
                onCadastro = { nav.navigate(Rotas.CADASTRO_CLIENTE) }
            )
        }
        composable(Rotas.CADASTRO_CLIENTE) {
            CadastroClienteScreen(
                onCadastrado = { nav.popBackStack() },
                onPrestador = { nav.navigate(Rotas.CADASTRO_PRESTADOR) },
                onVoltar = { nav.popBackStack() }
            )
        }
        composable(Rotas.CADASTRO_PRESTADOR) {
            CadastroPrestadorScreen(
                onCadastrado = { nav.popBackStack(Rotas.LOGIN, false) },
                onVoltar = { nav.popBackStack() }
            )
        }

        // TODO: trocar HomeProvisoria pela tela Home (feed de anúncios) quando ela for feita
        composable(Rotas.HOME) {
            HomeProvisoria(
                onSair = {
                    nav.navigate(Rotas.LOGIN) { popUpTo(Rotas.HOME) { inclusive = true } }
                }
            )
        }
    }
}