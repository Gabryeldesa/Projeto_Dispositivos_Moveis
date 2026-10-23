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
import com.example.dracma.ui.HomeScreen
import com.example.dracma.ui.LoginScreen
import data.Repositorio

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

    NavHost(navController = nav, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onEntrar = {
                    nav.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onCadastro = { nav.navigate("cadastro") }
            )
        }
        composable("cadastro") {
            CadastroClienteScreen(
                onCadastrado = { nav.popBackStack() },
                onPrestador = { nav.navigate("cadastro_prestador") },
                onVoltar = { nav.popBackStack() }
            )
        }
        composable("cadastro_prestador") {
            CadastroPrestadorScreen(
                onCadastrado = { nav.popBackStack("login", false) },
                onVoltar = { nav.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                onSair = {
                    nav.navigate("login") { popUpTo("home") { inclusive = true } }
                },
                onAnuncio = { /* tela de serviço ainda não existe */ },
                onCategoria = { /* tela de categoria ainda não existe */ }
            )
        }
    }
}