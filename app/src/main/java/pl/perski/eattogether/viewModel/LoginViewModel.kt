package pl.perski.eattogether.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import pl.perski.eattogether.model.AccountModel
import pl.perski.eattogether.source.remote.service.ILoginApiService
import pl.perski.eattogether.source.remote.service.LoginApiService
import pl.perski.eattogether.utils.*

interface ILoginViewModel {
    val progress: LiveData<Boolean>
    val errors: LiveData<ErrorMessage>
    val tokenHeader: LiveData<String>
    fun signIn(data: AccountModel)
}

class LoginViewModel(
    private val apiService: ILoginApiService = LoginApiService()
) : ViewModel(),
    ILoginViewModel {
    override val progress: LiveData<Boolean>
        get() = progressData

    override val errors: LiveData<ErrorMessage>
        get() = errorsData
    override val tokenHeader: LiveData<String>
        get() = tokenHeaderData

    private var disposable: Disposable? = null
    private val progressData = MutableLiveData<Boolean>()
    private val errorsData = MutableLiveData<ErrorMessage>()
    private val tokenHeaderData = MutableLiveData<String>()

    override fun signIn(data: AccountModel) {

        disposable?.dispose()

        disposable = apiService.signIn(data)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                if (it.isSuccessful) {
                    tokenHeaderData.value = it.headers()["Authorization"]
                } else {
                    errorsData.value = ErrorMessage("Error.")
                }
            }
    }

}
