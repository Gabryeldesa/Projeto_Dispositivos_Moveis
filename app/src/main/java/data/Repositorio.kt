package data

import android.content.Context
import model.Anuncio
import model.Categoria
import model.Cliente
import model.Contratacao
import model.Endereco
import model.Prestador
import model.Usuario
import model.criarServico
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// "object" = só existe uma instância no app inteiro.
// Os dados ficam em memória e são gravados em um arquivo JSON a cada mudança.
object Repositorio {

    private val usuarios = mutableListOf<Usuario>()
    private val senhas = mutableMapOf<String, String>() // email -> senha
    private val anuncios = mutableListOf<Anuncio>()
    private val contratacoes = mutableListOf<Contratacao>()
    private var proximoId = 1

    // Arquivo onde os dados são salvos (definido em iniciar)
    private var arquivo: File? = null

    // Nullable: null significa "ninguém logado". Só o Repositorio altera.
    var usuarioLogado: Usuario? = null
        private set

    // ---------- Inicialização e persistência ----------

    // Chamar UMA vez, no MainActivity, antes de mostrar qualquer tela
    fun iniciar(context: Context) {
        if (arquivo != null) return // já iniciado

        val f = File(context.applicationContext.filesDir, "dracma.json")
        arquivo = f

        val carregou = f.exists() && carregar(f)
        if (!carregou) {
            // Primeira vez (ou arquivo ilegível): começa com os dados iniciais
            limpar()
            carregarDadosIniciais()
            salvar()
        }
    }

    private fun limpar() {
        usuarios.clear()
        senhas.clear()
        anuncios.clear()
        contratacoes.clear()
        proximoId = 1
    }

    private fun enderecoParaJson(e: Endereco): JSONObject {
        val o = JSONObject()
        o.put("cep", e.cep)
        o.put("rua", e.rua)
        o.put("complemento", e.complemento)
        o.put("numero", e.numero)
        o.put("bairro", e.bairro)
        o.put("cidade", e.cidade)
        o.put("estado", e.estado)
        return o
    }

    private fun enderecoDeJson(o: JSONObject): Endereco {
        return Endereco(
            cep = o.getString("cep"),
            rua = o.getString("rua"),
            complemento = o.getString("complemento"),
            numero = o.getString("numero"),
            bairro = o.getString("bairro"),
            cidade = o.getString("cidade"),
            estado = o.getString("estado")
        )
    }

    // Grava tudo no arquivo. Chamada depois de cada mudança nos dados.
    @Suppress("LanguageDetectionInspection")
    private fun salvar() {
        val f = arquivo ?: return
        try {
            val raiz = JSONObject()
            raiz.put("proximoId", proximoId)

            val arrUsuarios = JSONArray()
            usuarios.forEach { u ->
                val o = JSONObject()
                o.put("id", u.id)
                o.put("nome", u.nome)
                o.put("email", u.email)
                o.put("nomeUsuario", u.nomeUsuario)
                o.put("telefone", u.telefone)
                o.put("endereco", enderecoParaJson(u.endereco))
                when (u) {
                    is Cliente -> {
                        o.put("tipo", "cliente")
                        o.put("cpf", u.cpf)
                        o.put("saldo", u.consultarSaldo())
                    }
                    is Prestador -> {
                        o.put("tipo", "prestador")
                        o.put("cpfCnpj", u.cpfCnpj)
                        o.put("descricao", u.descricao)
                        o.put("nota", u.nota)
                        o.put("areaOutros", u.areaOutros)
                        val cats = JSONArray()
                        u.categorias.forEach { cats.put(it.name) }
                        o.put("categorias", cats)
                    }
                }
                arrUsuarios.put(o)
            }
            raiz.put("usuarios", arrUsuarios)

            val objSenhas = JSONObject()
            senhas.forEach { (email, senha) -> objSenhas.put(email, senha) }
            raiz.put("senhas", objSenhas)

            val arrAnuncios = JSONArray()
            anuncios.forEach { a ->
                val o = JSONObject()
                o.put("id", a.id)
                o.put("prestadorId", a.prestador.id)
                o.put("titulo", a.servico.titulo)
                o.put("categoria", a.servico.categoria.name)
                o.put("valor", a.servico.valor)
                o.put("descricao", a.descricao)
                o.put("ativo", a.ativo)
                val notas = JSONArray()
                a.listaNotas().forEach { notas.put(it) }
                o.put("notas", notas)
                arrAnuncios.put(o)
            }
            raiz.put("anuncios", arrAnuncios)

            val arrContratacoes = JSONArray()
            contratacoes.forEach { c ->
                val o = JSONObject()
                o.put("id", c.id)
                o.put("clienteId", c.cliente.id)
                o.put("anuncioId", c.anuncio.id)
                o.put("valorPago", c.valorPago)
                o.put("cashback", c.cashback)
                arrContratacoes.put(o)
            }
            raiz.put("contratacoes", arrContratacoes)

            // Escreve em um arquivo temporário e troca, para não corromper o arquivo
            val temporario = File(f.parentFile, "dracma.json.tmp")
            temporario.writeText(raiz.toString())
            temporario.renameTo(f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Lê o arquivo e recria os objetos. Devolve false se algo estiver errado.
    private fun carregar(f: File): Boolean {
        return try {
            val raiz = JSONObject(f.readText())
            limpar()
            proximoId = raiz.getInt("proximoId")

            val arrUsuarios = raiz.getJSONArray("usuarios")
            for (i in 0 until arrUsuarios.length()) {
                val o = arrUsuarios.getJSONObject(i)
                val endereco = enderecoDeJson(o.getJSONObject("endereco"))

                if (o.getString("tipo") == "cliente") {
                    val cliente = Cliente(
                        o.getInt("id"), o.getString("nome"), o.getString("email"),
                        o.getString("nomeUsuario"), o.getString("telefone"),
                        endereco, o.getString("cpf")
                    )
                    cliente.adicionarCoins(o.optDouble("saldo", 0.0))
                    usuarios.add(cliente)
                } else {
                    val arrCats = o.getJSONArray("categorias")
                    val categorias = mutableListOf<Categoria>()
                    for (j in 0 until arrCats.length()) {
                        categorias.add(Categoria.valueOf(arrCats.getString(j)))
                    }
                    usuarios.add(
                        Prestador(
                            id = o.getInt("id"),
                            nomeEmpresa = o.getString("nome"),
                            email = o.getString("email"),
                            nomeUsuario = o.getString("nomeUsuario"),
                            telefone = o.getString("telefone"),
                            endereco = endereco,
                            cpfCnpj = o.getString("cpfCnpj"),
                            descricao = o.getString("descricao"),
                            categorias = categorias,
                            nota = o.optDouble("nota", 0.0),
                            areaOutros = o.optString("areaOutros", "")
                        )
                    )
                }
            }

            val objSenhas = raiz.getJSONObject("senhas")
            objSenhas.keys().forEach { email -> senhas[email] = objSenhas.getString(email) }

            val arrAnuncios = raiz.getJSONArray("anuncios")
            for (i in 0 until arrAnuncios.length()) {
                val o = arrAnuncios.getJSONObject(i)
                val prestador = usuarios.filterIsInstance<Prestador>()
                    .first { it.id == o.getInt("prestadorId") }
                val servico = criarServico(
                    o.getString("titulo"),
                    Categoria.valueOf(o.getString("categoria")),
                    o.getDouble("valor")
                )
                val anuncio = Anuncio(o.getInt("id"), prestador, servico, o.getString("descricao"))
                anuncio.ativo = o.optBoolean("ativo", true)
                val notas = o.getJSONArray("notas")
                for (j in 0 until notas.length()) {
                    anuncio.avaliar(notas.getDouble(j))
                }
                anuncios.add(anuncio)
            }

            val arrContratacoes = raiz.getJSONArray("contratacoes")
            for (i in 0 until arrContratacoes.length()) {
                val o = arrContratacoes.getJSONObject(i)
                val cliente = usuarios.filterIsInstance<Cliente>()
                    .first { it.id == o.getInt("clienteId") }
                val anuncio = anuncios.first { it.id == o.getInt("anuncioId") }
                contratacoes.add(
                    Contratacao(
                        o.getInt("id"), cliente, anuncio,
                        o.getDouble("valorPago"), o.getDouble("cashback")
                    )
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun novoId(): Int {
        return proximoId++
    }

    // ---------- Validações ----------

    private fun emailValido(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun buscarPorEmail(email: String): Usuario? {
        return usuarios.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    // Devolve a mensagem de erro, ou null quando está tudo certo
    private fun validarAcesso(email: String, senha: String, confirmacao: String): String? {
        if (!emailValido(email)) return "Digite um e-mail válido"
        if (buscarPorEmail(email) != null) return "E-mail já cadastrado"
        if (senha.length < 4) return "A senha precisa ter ao menos 4 caracteres"
        if (senha != confirmacao) return "As senhas não conferem"
        return null
    }

    // ---------- Cadastro ----------

    fun cadastrarCliente(
        nome: String,
        email: String,
        nomeUsuario: String,
        telefone: String,
        endereco: Endereco,
        cpf: String,
        senha: String,
        confirmacao: String
    ): String? {
        if (nome.isBlank() || nomeUsuario.isBlank() || cpf.isBlank()) {
            return "Preencha todos os campos obrigatórios"
        }
        val erro = validarAcesso(email, senha, confirmacao)
        if (erro != null) return erro

        usuarios.add(Cliente(novoId(), nome, email, nomeUsuario, telefone, endereco, cpf))
        senhas[email.lowercase()] = senha
        salvar()
        return null
    }

    fun cadastrarPrestador(
        nomeEmpresa: String,
        email: String,
        nomeUsuario: String,
        telefone: String,
        endereco: Endereco,
        cpfCnpj: String,
        descricao: String,
        categorias: List<Categoria>,
        senha: String,
        confirmacao: String,
        areaOutros: String = ""
    ): String? {
        if (nomeEmpresa.isBlank() || nomeUsuario.isBlank() || cpfCnpj.isBlank()) {
            return "Preencha todos os campos obrigatórios"
        }
        if (categorias.isEmpty()) return "Escolha ao menos uma área de atuação"
        if (categorias.contains(Categoria.OUTROS) && areaOutros.isBlank()) {
            return "Descreva sua área de atuação em \"Outros\""
        }
        val erro = validarAcesso(email, senha, confirmacao)
        if (erro != null) return erro

        usuarios.add(
            Prestador(
                id = novoId(),
                nomeEmpresa = nomeEmpresa,
                email = email,
                nomeUsuario = nomeUsuario,
                telefone = telefone,
                endereco = endereco,
                cpfCnpj = cpfCnpj,
                descricao = descricao,
                categorias = categorias,
                areaOutros = areaOutros.trim()
            )
        )
        senhas[email.lowercase()] = senha
        salvar()
        return null
    }

    // ---------- Login ----------

    fun login(email: String, senha: String): Usuario? {
        val usuario = buscarPorEmail(email) ?: return null
        if (senhas[email.lowercase()] != senha) return null
        usuarioLogado = usuario
        return usuario
    }

    fun sair() {
        usuarioLogado = null
    }

    // ---------- Feed ----------

    // Valor padrão: sem argumentos, devolve todos os anúncios
    fun buscarAnuncios(texto: String = "", categoria: Categoria? = null): List<Anuncio> {
        return anuncios.filter { anuncio ->
            val bateTexto = texto.isBlank() ||
                    anuncio.servico.titulo.contains(texto, ignoreCase = true) ||
                    anuncio.prestador.nome.contains(texto, ignoreCase = true)
            val bateCategoria = categoria == null || anuncio.servico.categoria == categoria
            anuncio.ativo && bateTexto && bateCategoria
        }
    }

    // ---------- Contratar, avaliar e publicar ----------

    private fun buscarAnuncioPorId(id: Int): Anuncio? {
        return anuncios.firstOrNull { it.id == id }
    }

    // Devolve a mensagem de erro, ou null quando deu certo
    fun contratar(anuncioId: Int): String? {
        val logado = usuarioLogado
        if (logado == null) return "Faça login para contratar"
        if (logado !is Cliente) return "Apenas clientes podem contratar"

        val anuncio = buscarAnuncioPorId(anuncioId)
        if (anuncio == null || !anuncio.ativo) return "Anúncio não encontrado"

        val cashback = anuncio.servico.calcularCashback()
        logado.adicionarCoins(cashback)
        contratacoes.add(Contratacao(novoId(), logado, anuncio, anuncio.servico.valor, cashback))
        salvar()
        return null
    }

    fun minhasContratacoes(): List<Contratacao> {
        val logado = usuarioLogado ?: return emptyList()
        return contratacoes.filter { it.cliente.id == logado.id }
    }

    fun avaliarAnuncio(anuncioId: Int, nota: Double): String? {
        val logado = usuarioLogado
        if (logado !is Cliente) return "Faça login como cliente para avaliar"
        if (nota < 0.0 || nota > 5.0) return "A nota deve ser de 0 a 5"

        val anuncio = buscarAnuncioPorId(anuncioId) ?: return "Anúncio não encontrado"
        val jaContratou = contratacoes.any { it.cliente.id == logado.id && it.anuncio.id == anuncio.id }
        if (!jaContratou) return "Você só pode avaliar serviços que contratou"

        anuncio.avaliar(nota)
        salvar()
        return null
    }

    fun publicarAnuncio(
        titulo: String,
        categoria: Categoria,
        valor: Double,
        descricao: String = ""
    ): String? {
        val logado = usuarioLogado
        if (logado !is Prestador) return "Apenas prestadores podem publicar anúncios"
        if (titulo.isBlank()) return "Informe o título do serviço"
        if (valor <= 0.0) return "Informe um valor maior que zero"
        if (!logado.categorias.contains(categoria)) return "Você não atua nessa categoria"

        val descricaoFinal = if (descricao.isBlank()) logado.descricao else descricao
        anuncios.add(Anuncio(novoId(), logado, criarServico(titulo, categoria, valor), descricaoFinal))
        salvar()
        return null
    }

    fun meusAnuncios(): List<Anuncio> {
        val logado = usuarioLogado ?: return emptyList()
        return anuncios.filter { it.prestador.id == logado.id }
    }

    // ---------- Dados iniciais (só na primeira vez; senha de todos: 1234) ----------

    private fun enderecoPadrao(): Endereco {
        return Endereco("74000-000", "Rua das Flores", "", "10", "Centro", "Aparecida de Goiânia", "GO")
    }

    private fun criarPrestadorFicticio(
        nomeEmpresa: String,
        email: String,
        categoria: Categoria,
        descricao: String
    ): Prestador {
        val prestador = Prestador(
            id = novoId(),
            nomeEmpresa = nomeEmpresa,
            email = email,
            nomeUsuario = email.substringBefore("@"),
            telefone = "62 90000-0000",
            endereco = enderecoPadrao(),
            cpfCnpj = "00.000.000/0001-00",
            descricao = descricao,
            categorias = listOf(categoria)
        )
        usuarios.add(prestador)
        senhas[email] = "1234"
        return prestador
    }

    private fun publicarFicticio(
        prestador: Prestador,
        titulo: String,
        categoria: Categoria,
        valor: Double,
        notas: List<Double>
    ) {
        val anuncio = Anuncio(novoId(), prestador, criarServico(titulo, categoria, valor), prestador.descricao)
        notas.forEach { anuncio.avaliar(it) }
        anuncios.add(anuncio)
    }

    private fun carregarDadosIniciais() {
        usuarios.add(
            Cliente(
                novoId(), "Maria Silva", "cliente@email.com", "mariasilva",
                "62 99999-0000", enderecoPadrao(), "000.000.000-00"
            )
        )
        senhas["cliente@email.com"] = "1234"

        val hidraulica = criarPrestadorFicticio(
            "Hidráulica Silva", "hidraulica@email.com", Categoria.ENCANACAO,
            "Conserto de vazamentos e desentupimentos"
        )
        val eletrica = criarPrestadorFicticio(
            "Eletro Center", "eletro@email.com", Categoria.ELETRICISTA,
            "Instalações e reparos elétricos residenciais"
        )
        val beleza = criarPrestadorFicticio(
            "Studio Bella", "bella@email.com", Categoria.ESTETICA,
            "Manicure, maquiagem e cabelo"
        )
        val limpeza = criarPrestadorFicticio(
            "Limpa Tudo", "limpatudo@email.com", Categoria.DOMESTICOS,
            "Limpeza geral e lavanderia"
        )
        val obras = criarPrestadorFicticio(
            "Obra Certa", "obracerta@email.com", Categoria.CONSTRUCAO,
            "Pedreiro e pintor"
        )

        publicarFicticio(hidraulica, "Conserto de vazamentos", Categoria.ENCANACAO, 150.0, listOf(5.0, 4.0))
        publicarFicticio(hidraulica, "Desentupimento de tubulações", Categoria.ENCANACAO, 200.0, listOf(5.0))
        publicarFicticio(eletrica, "Instalação elétrica residencial", Categoria.ELETRICISTA, 300.0, listOf(4.0, 4.0))
        publicarFicticio(beleza, "Manicure e pedicure", Categoria.ESTETICA, 80.0, listOf(5.0, 5.0))
        publicarFicticio(limpeza, "Limpeza geral", Categoria.DOMESTICOS, 120.0, listOf(4.0, 5.0))
        publicarFicticio(obras, "Pintura de paredes", Categoria.CONSTRUCAO, 450.0, emptyList())
    }
}