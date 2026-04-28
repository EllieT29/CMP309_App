package com.example.bloom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom.network.ConnectivityObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

//ViewModel for connectivity observer
class MainViewModel(networkObserver: ConnectivityObserver): ViewModel() {

    //Get the current network connectivity status
    val isConnected = networkObserver.isConnectedFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = false
    )
}