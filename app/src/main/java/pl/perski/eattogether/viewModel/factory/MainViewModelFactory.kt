package pl.perski.eattogether.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.perski.eattogether.source.remote.service.EventApiService
import pl.perski.eattogether.viewModel.MainViewModel

class MainViewModelFactory(private val apiKey: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(EventApiService(), apiKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}