package pl.perski.eattogether.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import pl.perski.eattogether.model.EventModel
import pl.perski.eattogether.source.remote.service.IEventApiService
import pl.perski.eattogether.utils.*

interface IMainViewModel {
    val events: LiveData<List<EventModel>>
    val progress: LiveData<Boolean>
    val errors: LiveData<ErrorMessage>
    val eventsCount: Int

    fun getEventsData()
}

class MainViewModel(
    private val apiService: IEventApiService, private val apiKey: String
) : ViewModel(), IMainViewModel {
    override val events: LiveData<List<EventModel>>
        get() = eventsData
    override val progress: LiveData<Boolean>
        get() = progressData
    override val errors: LiveData<ErrorMessage>
        get() = errorsData
    override val eventsCount: Int
        get() = eventsData.value?.count() ?: 0

    private val eventsData = MutableLiveData<List<EventModel>>()
    private val progressData = MutableLiveData<Boolean>(false)
    private val errorsData = MutableLiveData<ErrorMessage>()

    private var disposable: Disposable? = null
    override fun getEventsData() {
        disposable?.dispose()

//todo add error handling
        disposable = apiService.getEvents(apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                eventsData.value = it
            }
    }
}
