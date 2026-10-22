package model

// Categorias do app (iguais às do Figma) com os serviços de cada uma
enum class Categoria(val titulo: String, val servicos: List<String>) {
    ENCANACAO(
        "Encanação",
        listOf(
            "Desentupimento de tubulações",
            "Conserto de vazamentos",
            "Instalação de sistemas hidráulicos",
            "Instalação e manutenção de sistemas de drenagem"
        )
    ),
    ELETRICISTA(
        "Elétricos",
        listOf(
            "Instalação elétrica residencial",
            "Manutenção preventiva",
            "Reparos elétricos",
            "Projetos elétricos"
        )
    ),
    ESTETICA(
        "Estética",
        listOf("Manicure", "Pedicure", "Maquiagem", "Depilação", "Cortes, luzes e coloração de cabelos")
    ),
    DOMESTICOS(
        "Domésticos",
        listOf(
            "Limpeza geral",
            "Lavanderia",
            "Cozinheira",
            "Cuidados com animais de estimação",
            "Jardinagem e cuidados com áreas externas"
        )
    ),
    CONSTRUCAO(
        "Construção",
        listOf("Pedreiro", "Servente", "Mestre de obra", "Pintor")
    ),
    OUTROS(
        "Outros",
        listOf(
            "Manutenção de eletrodomésticos",
            "Chaveiro",
            "Manutenção de ar-condicionado",
            "Dedetização",
            "Mais..."
        )
    )
}

// Endereço (campos iguais aos das telas de cadastro)
class Endereco(
    val cep: String,
    val rua: String,
    val complemento: String,
    val numero: String,
    val bairro: String,
    val cidade: String,
    val estado: String
)

// Classe base: "open" permite herança (como a classe Carro da aula)
open class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val nomeUsuario: String,
    val telefone: String,
    val endereco: Endereco
)

// Herança: Cliente é um Usuario
class Cliente(
    id: Int,
    nome: String,
    email: String,
    nomeUsuario: String,
    telefone: String,
    endereco: Endereco,
    val cpf: String
) : Usuario(id, nome, email, nomeUsuario, telefone, endereco) {

    // Encapsulamento: o saldo só pode ser alterado por dentro da classe
    private var saldoDracCoins: Double = 0.0

    fun adicionarCoins(valor: Double) {
        if (valor > 0) {
            saldoDracCoins += valor
        }
    }

    fun consultarSaldo(): Double {
        return saldoDracCoins
    }
}

// Herança: Prestador também é um Usuario (o "nome" dele é o nome da empresa)
class Prestador(
    id: Int,
    nomeEmpresa: String,
    email: String,
    nomeUsuario: String,
    telefone: String,
    endereco: Endereco,
    val cpfCnpj: String,
    val descricao: String,
    val categorias: List<Categoria>,
    var nota: Double = 0.0,
    val areaOutros: String = ""
) : Usuario(id, nomeEmpresa, email, nomeUsuario, telefone, endereco) {

    fun resumo(): String = """
        Empresa: $nome
        Áreas: ${categorias.joinToString { if (it == Categoria.OUTROS && areaOutros.isNotBlank()) "Outros: $areaOutros" else it.titulo }}
        Nota: $nota
    """.trimIndent()
}