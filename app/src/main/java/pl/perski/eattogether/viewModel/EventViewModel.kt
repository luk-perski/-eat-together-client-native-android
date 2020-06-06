package pl.perski.eattogether.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import pl.perski.eattogether.model.EventModel
import pl.perski.eattogether.source.remote.service.IEventApiService
import pl.perski.eattogether.utils.*

interface IEventViewModel {
    val result: LiveData<String>
    val progress: LiveData<Boolean>
    val errors: LiveData<ErrorMessage>

    fun joinEvent()
    fun leftFromEvent()
    fun deleteEvent()
    fun addEvent(eventModel: EventModel)
}

class EventViewModel(
    private val apiService: IEventApiService, private val apiKey: String, private val eventId: Int
) : ViewModel(), IEventViewModel {
    override val result: LiveData<String>
        get() = resultData
    override val progress: LiveData<Boolean>
        get() = progressData
    override val errors: LiveData<ErrorMessage>
        get() = errorsData

    private val resultData = MutableLiveData<String>()
    private val progressData = MutableLiveData<Boolean>(false)
    private val errorsData = MutableLiveData<ErrorMessage>()

    private var disposable: Disposable? = null

    override fun joinEvent() {
        disposable?.dispose()

        disposable = apiService.jointToEvent(eventId, apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                resultData.value = it
            }
    }

    override fun leftFromEvent() {
        disposable?.dispose()

        disposable = apiService.leftFromEvent(eventId, apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                resultData.value = it
            }
    }

    override fun deleteEvent() {
        disposable?.dispose()

        disposable = apiService.deleteEvent(eventId, apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                resultData.value = it
            }
    }

    override fun addEvent(eventModel: EventModel) {
        disposable?.dispose()

        disposable = apiService.addEvent(eventModel, apiKey)
            .subscribeOnIOThread()
            .observeOnMainThread()
            .withProgress(progressData)
            .showErrorMessages(errorsData)
            .subscribe {
                resultData.value = "Going to ${it.placeName} has been added."
            }
    }

}