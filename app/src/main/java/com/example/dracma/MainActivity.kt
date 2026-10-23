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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.dracma.ui.AvaliarPrestadorScreen
import com.example.dracma.ui.MeusPedidosScreen
import com.example.dracma.ui.CadastroClienteScreen
import com.example.dracma.ui.ChatScreen
import com.example.dracma.ui.ConversasScreen
import com.example.dracma.ui.CadastroPrestadorScreen
import com.example.dracma.ui.DracmaTheme
import com.example.dracma.ui.HomeScreen
import com.example.dracma.ui.LoginScreen
import com.example.dracma.ui.PerfilPrestadorScreen
import data.Repositorio

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
    const val MEUS_PEDIDOS = "meus_pedidos"
    const val AVALIAR_PRESTADOR = "avaliar_prestador/{anuncioId}"

    // Montam a rota já com o id dentro. Use estas funções na hora de navegar.
    fun perfilPrestador(id: Int) = "perfil_prestador/$id"
    fun chat(conversaId: Int) = "chat/$conversaId"
    fun avaliarPrestador(anuncioId: Int) = "avaliar_prestador/$anuncioId"
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
    val context = LocalContext.current

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
                // TODO: ligar na tela 5 (Serviços da categoria) quando ela ficar pronta
                onCategoria = { },
                onChat = { nav.navigate(Rotas.CONVERSAS) },
                onMeusPedidos = { nav.navigate(Rotas.MEUS_PEDIDOS) }
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
                // TEMPORÁRIO: contrata direto até a tela de contratar serviço (tela 7) ficar pronta.
                // Serve para o pedido aparecer em "Meus pedidos". Troque pela tela 7 depois.
                onContratar = { anuncioId ->
                    val erro = Repositorio.contratar(anuncioId)
                    Toast.makeText(context, erro ?: "Serviço contratado!", Toast.LENGTH_SHORT).show()
                }
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

        // Meus pedidos: serviços que o cliente contratou
        composable(Rotas.MEUS_PEDIDOS) {
            MeusPedidosScreen(
                onVoltar = { nav.popBackStack() },
                onAvaliar = { anuncioId -> nav.navigate(Rotas.avaliarPrestador(anuncioId)) }
            )
        }

        // Avaliar prestador: recebe o id do anúncio contratado
        composable(
            route = Rotas.AVALIAR_PRESTADOR,
            arguments = listOf(navArgument("anuncioId") { type = NavType.IntType })
        ) { entrada ->
            val anuncioId = entrada.arguments?.getInt("anuncioId") ?: 0
            AvaliarPrestadorScreen(
                anuncioId = anuncioId,
                onVoltar = { nav.popBackStack() },
                onConcluido = { nav.popBackStack() }
            )
        }
    }
}