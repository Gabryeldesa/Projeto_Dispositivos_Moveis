package model

// Registro de um serviço contratado por um cliente
class Contratacao(
    val id: Int,
    val cliente: Cliente,
    val anuncio: Anuncio,
    val valorPago: Double,
    val cashback: Double
)