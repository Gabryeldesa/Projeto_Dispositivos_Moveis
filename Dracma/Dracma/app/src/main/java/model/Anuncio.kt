package model

// Interface: "contrato" de quem pode ser avaliado
interface Avaliavel {
    fun avaliar(nota: Double)
    fun notaMedia(): Double
}

class Anuncio(
    val id: Int,
    val prestador: Prestador,
    val servico: Servico,
    val descricao: String
) : Avaliavel {

    var ativo: Boolean = true

    // Encapsulamento: a lista de notas fica escondida
    private val notas = mutableListOf<Double>()

    override fun avaliar(nota: Double) {
        if (nota >= 0.0 && nota <= 5.0) {
            notas.add(nota)
        }
    }

    override fun notaMedia(): Double {
        return if (notas.isEmpty()) 0.0 else notas.average()
    }

    // Devolve uma cópia das notas (usada para salvar no arquivo)
    fun listaNotas(): List<Double> {
        return notas.toList()
    }

    fun resumo(): String = """
        ${servico.titulo} - ${prestador.nome}
        Categoria: ${servico.categoria.titulo}
        Valor: R$ ${servico.valor}
        Nota: ${notaMedia()}
    """.trimIndent()
}