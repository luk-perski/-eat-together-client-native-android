package pl.perski.eattogether.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import pl.perski.eattogether.model.AccountModel
import pl.perski.eattogether.model.AddAccountModel
import pl.perski.eattogether.model.LocationModel
import pl.perski.eattogether.model.UserModel
import pl.perski.eattogether.source.remote.service.ILoginApiService
import pl.perski.eattogether.source.remote.service.IRegisterApiService
import pl.perski.eattogether.source.remote.service.LoginApiService
import pl.perski.eattogether.source.remote.service.RegisterApiService
import pl.perski.eattogether.utils.*


interface IRegisterViewModel {
    val progress: LiveData<Boolean>
    val message: LiveData<String>
    val tokenHeader: LiveData<String>
    val errors: LiveData<ErrorMessage>
    val apiError: LiveData<ErrorMessage>
    val user: LiveData<UserModel>
    fun signUp(data: AddAccountModel)
    fun signIn(data: AccountModel)
    fun update(data: UserModel, apiKey: String)
    fun getUser(apiKey: String)
}

class RegisterViewModel(
    private val registerApiService: IRegisterApiService = RegisterApiService(),
    private val loginApiService: ILoginApiService = LoginApiService()
) :
    ViewModel(), IRegisterViewModel {

    override val progress: LiveData<Boolean>
        get() = progressData
    override val message: LiveData<String>
        get() = messageData
    override val tokenHeader: LiveData<String>
        get() = tokenHeaderData
    override val errors: LiveData<ErrorMessage>
        get() = errorsData
    override val apiError: LiveData<ErrorMessage>
        get() = apiErrorData
    override val user: LiveData<UserModel>
        get() = userData

    private var disposable: Disposable? = null
    private val progressData = MutableLiveData<Boolean>()
    private val tokenHeaderData = MutableLiveData<String>()
    private val messageData = MutableLiveData<String>()
    private var errorsData = MutableLiveData<ErrorMessage>()
    private var apiErrorData = MutableLiveData<ErrorMessage>()
    private val userData = MutableLiveData<UserModel>()


    override fun signUp(data: AddAccountModel) {
        disposable?.dispose()

        disposable = registerApiService.signUp(data)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                if (it.isSuccessful) {
                    signIn(
                        AccountModel(
                            email = data.accountData.email,
                            password = data.accountData.password
                        )
                    )
                } else {
                    errorsData.value = ErrorMessage("Error.")
                }
            }
    }

    override fun signIn(data: AccountModel) {
        disposable?.dispose()

        disposable = loginApiService.signIn(data)
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

    override fun update(data: UserModel, apiKey: String) {
        disposable?.dispose()

        disposable = registerApiService.update(data, apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                messageData.value = "User has been updated."
            }
    }

    override fun getUser(apiKey: String) {
        disposable?.dispose()
        disposable = registerApiService.getUser(apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                userData.value = it
            }
    }
}