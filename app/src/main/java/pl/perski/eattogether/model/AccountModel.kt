package pl.perski.eattogether.model

data class AccountModel(
    val id: Int? = null,
    val email: String,
    val password: String,
    val eventHistory: String? = null
)