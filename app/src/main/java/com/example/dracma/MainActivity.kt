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
import com.example.dracma.ui.ChatScreen
import com.example.dracma.ui.ConversasScreen
import com.example.dracma.ui.CadastroPrestadorScreen
import com.example.dracma.ui.ContratarServicoScreen
import com.example.dracma.ui.DracmaTheme
import com.example.dracma.ui.HomeScreen
import com.example.dracma.ui.LoginScreen
import com.example.dracma.ui.PerfilPrestadorScreen
import com.example.dracma.ui.ServicosCategoriaScreen
import data.Repositorio
import model.Categoria

// Nomes das rotas em um só lugar (evita erro de digitação nas strings)
object Rotas {
    const val LOGIN = "login"
    const val CADASTRO_CLIENTE = "cadastro_cliente"
    const val CADASTRO_PRESTADOR = "cadastro_prestador"
    const val HOME = "home"

    // O {prestadorId} é um argumento: a rota muda conforme o prestador escolhido
    const val PERFIL_PRESTADOR = "perfil_prestador/{prestadorId}"
    const val CHAT = "chat/{conversaId}"
    const val CONVERSAS = "conversas"

    // Montam a rota já com o id dentro. Use estas funções na hora de navegar.
    fun perfilPrestador(id: Int) = "perfil_prestador/$id"
    fun chat(conversaId: Int) = "chat/$conversaId"

    // Telas 5 e 7
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

        // Telas 3 e 4 - Home "Para você" e Todos os serviços (duas abas na mesma tela)
        composable(Rotas.HOME) {
            HomeScreen(
                onSair = {
                    nav.navigate(Rotas.LOGIN) { popUpTo(Rotas.HOME) { inclusive = true } }
                },
                // Clicar num anúncio do feed abre o perfil de quem oferece o serviço
                onAnuncio = { anuncioId ->
                    val anuncio = Repositorio.buscarAnuncios().firstOrNull { it.id == anuncioId }
                    if (anuncio != null) {
                        nav.navigate(Rotas.perfilPrestador(anuncio.prestador.id))
                    }
                },
                onCategoria = { categoria -> nav.navigate(Rotas.servicosCategoria(categoria)) },
                onChat = { nav.navigate(Rotas.CONVERSAS) }
            )
        }

        // Lista de conversas: a porta de entrada do chat
        composable(Rotas.CONVERSAS) {
            ConversasScreen(
                onVoltar = { nav.popBackStack() },
                onAbrirConversa = { conversaId -> nav.navigate(Rotas.chat(conversaId)) }
            )
        }

        // Tela 6 - Perfil do prestador
        composable(
            route = Rotas.PERFIL_PRESTADOR,
            arguments = listOf(navArgument("prestadorId") { type = NavType.IntType })
        ) { entrada ->
            // Pega o id que veio dentro da rota
            val prestadorId = entrada.arguments?.getInt("prestadorId") ?: 0
            PerfilPrestadorScreen(
                prestadorId = prestadorId,
                onVoltar = { nav.popBackStack() },
                onConversar = { id ->
                    // Abre (ou reaproveita) a conversa com esse prestador e vai para o chat
                    val conversa = Repositorio.abrirConversa(id)
                    if (conversa != null) {
                        nav.navigate(Rotas.chat(conversa.id))
                    }
                },
                onContratar = { anuncioId -> nav.navigate(Rotas.contratar(anuncioId)) }
            )
        }

        // Tela 9 - Chat
        composable(
            route = Rotas.CHAT,
            arguments = listOf(navArgument("conversaId") { type = NavType.IntType })
        ) { entrada ->
            val conversaId = entrada.arguments?.getInt("conversaId") ?: 0
            ChatScreen(
                conversaId = conversaId,
                onVoltar = { nav.popBackStack() }
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
                onPerfil = { nav.navigate(Rotas.perfilPrestador(it.prestador.id)) },
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