package pl.perski.eattogether.utils


class ErrorMessage(text: String) {

    private val message: String = text

    fun getMessage(): String {
        return message
    }
}