package pl.perski.eattogether.model

data class AddAccountModel(
    val accountData: AccountModel,
    val userData: UserModel
)