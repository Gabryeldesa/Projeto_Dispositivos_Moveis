package model

// Classe abstrata: não pode ser instanciada, só herdada
abstract class Servico(
    val titulo: String,
    val categoria: Categoria,
    val valor: Double
) {
    // "open" permite que cada categoria sobrescreva (Override)
    open fun percentualCashback(): Double {
        return 0.05
    }

    // Método abstrato: toda subclasse é obrigada a implementar
    abstract fun descricaoCategoria(): String

    fun calcularCashback(): Double {
        return valor * percentualCashback()
    }

    // Sobrecarga (Overload): mesmo nome, parâmetros diferentes
    fun calcularCashback(bonus: Double): Double {
        return calcularCashback() + bonus
    }
}

class ServicoEncanacao(titulo: String, valor: Double) :
    Servico(titulo, Categoria.ENCANACAO, valor) {
    override fun percentualCashback(): Double = 0.05
    override fun descricaoCategoria(): String = "Serviço hidráulico"
}

class ServicoEletrico(titulo: String, valor: Double) :
    Servico(titulo, Categoria.ELETRICISTA, valor) {
    override fun percentualCashback(): Double = 0.06
    override fun descricaoCategoria(): String = "Serviço elétrico"
}

class ServicoEstetica(titulo: String, valor: Double) :
    Servico(titulo, Categoria.ESTETICA, valor) {
    override fun percentualCashback(): Double = 0.08
    override fun descricaoCategoria(): String = "Serviço de beleza"
}

class ServicoDomestico(titulo: String, valor: Double) :
    Servico(titulo, Categoria.DOMESTICOS, valor) {
    override fun percentualCashback(): Double = 0.05
    override fun descricaoCategoria(): String = "Serviço doméstico"
}

class ServicoConstrucao(titulo: String, valor: Double) :
    Servico(titulo, Categoria.CONSTRUCAO, valor) {
    override fun percentualCashback(): Double = 0.04
    override fun descricaoCategoria(): String = "Serviço de construção"
}

class ServicoOutros(titulo: String, valor: Double) :
    Servico(titulo, Categoria.OUTROS, valor) {
    override fun percentualCashback(): Double = 0.03
    override fun descricaoCategoria(): String = "Outros serviços"
}

// Função que escolhe a subclasse certa a partir da categoria
fun criarServico(titulo: String, categoria: Categoria, valor: Double): Servico {
    return when (categoria) {
        Categoria.ENCANACAO -> ServicoEncanacao(titulo, valor)
        Categoria.ELETRICISTA -> ServicoEletrico(titulo, valor)
        Categoria.ESTETICA -> ServicoEstetica(titulo, valor)
        Categoria.DOMESTICOS -> ServicoDomestico(titulo, valor)
        Categoria.CONSTRUCAO -> ServicoConstrucao(titulo, valor)
        Categoria.OUTROS -> ServicoOutros(titulo, valor)
    }
}