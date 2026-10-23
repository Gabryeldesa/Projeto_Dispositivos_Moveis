package model

// Uma mensagem trocada dentro de uma conversa.
// "autorId" guarda quem escreveu, para a tela saber de que lado desenhar o balão.
// "instante" é a data/hora em milissegundos (System.currentTimeMillis()).
class Mensagem(
    val id: Int,
    val autorId: Int,
    val texto: String,
    val instante: Long
)

// Conversa entre um cliente e um prestador.
// Cada par cliente/prestador tem uma conversa só, que vai acumulando mensagens.
class Conversa(
    val id: Int,
    val cliente: Cliente,
    val prestador: Prestador
) {
    // Encapsulamento: a lista de mensagens fica escondida, igual às notas do Anuncio
    private val mensagens = mutableListOf<Mensagem>()

    fun adicionar(mensagem: Mensagem) {
        if (mensagem.texto.isNotBlank()) {
            mensagens.add(mensagem)
        }
    }

    // Devolve uma cópia, para ninguém alterar a lista de fora
    fun listaMensagens(): List<Mensagem> {
        return mensagens.toList()
    }

    // Usada na tela "Meus pedidos"/lista de conversas para mostrar a prévia
    fun ultimaMensagem(): Mensagem? {
        return mensagens.lastOrNull()
    }

    // Descobre quem é a outra pessoa da conversa, do ponto de vista de quem está logado
    fun outroParticipante(meuId: Int): Usuario {
        return if (meuId == cliente.id) prestador else cliente
    }
}
