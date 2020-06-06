package pl.perski.eattogether.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.perski.eattogether.source.remote.service.EventApiService
import pl.perski.eattogether.viewModel.EventViewModel

class EventViewModelFactory(private val apiKey: String, private val eventId: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(EventApiService(), apiKey, eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}