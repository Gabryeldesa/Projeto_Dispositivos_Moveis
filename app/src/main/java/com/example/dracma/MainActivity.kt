package com.example.dracma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dracma.ui.CadastroClienteScreen
import com.example.dracma.ui.CadastroPrestadorScreen
import com.example.dracma.ui.ContratarServicoScreen
import com.example.dracma.ui.DracmaTheme
import com.example.dracma.ui.HomeProvisoria
import com.example.dracma.ui.LoginScreen
import com.example.dracma.ui.ServicosCategoriaScreen
import data.Repositorio
import model.Categoria

// Nomes das rotas em um só lugar (evita erro de digitação nas strings)
object Rotas {
    const val LOGIN = "login"
    const val CADASTRO_CLIENTE = "cadastro_cliente"
    const val CADASTRO_PRESTADOR = "cadastro_prestador"
    const val HOME = "home"
    const val SERVICOS_CATEGORIA = "servicos_categoria/{categoria}"
    const val CONTRATAR = "contratar/{anuncioId}"

    fun servicosCategoria(categoria: Categoria) = "servicos_categoria/${categoria.name}"
    fun contratar(anuncioId: Int) = "contratar/$anuncioId"
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

        // Tela 5 - Serviços da categoria
        composable(
            Rotas.SERVICOS_CATEGORIA,
            arguments = listOf(navArgument("categoria") { type = NavType.StringType })
        ) { entry ->
            val categoria = Categoria.valueOf(entry.arguments?.getString("categoria") ?: "OUTROS")
            ServicosCategoriaScreen(
                categoria = categoria,
                onVoltar = { nav.popBackStack() },
                onPerfil = { /* TODO: tela 6 - Perfil do prestador */ },
                onContratar = { nav.navigate(Rotas.contratar(it.id)) }
            )
        }

        // Tela 7 - Contratar serviço
        composable(
            Rotas.CONTRATAR,
            arguments = listOf(navArgument("anuncioId") { type = NavType.IntType })
        ) { entry ->
            val anuncio = Repositorio.buscarAnuncios().firstOrNull {
                it.id == entry.arguments?.getInt("anuncioId")
            }
            if (anuncio != null) {
                ContratarServicoScreen(
                    anuncio = anuncio,
                    onVoltar = { nav.popBackStack() },
                    // TODO: tela 8 - ir para "Meus pedidos" em vez de voltar
                    onContratado = { nav.popBackStack() }
                )
            }
        }
    }
}