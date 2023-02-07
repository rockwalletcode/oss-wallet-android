package com.rockwallet.common.ui.base

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class RockWalletViewModel<State : RockWalletContract.State, Event : RockWalletContract.Event, Effect : RockWalletContract.Effect>(
    application: Application,
    private val savedStateHandle: SavedStateHandle? = null
) : AndroidViewModel(application), RockWalletEventHandler<Event> {

    private val initialState : State by lazy {
        if (savedStateHandle != null) {
            parseArguments(savedStateHandle)
        }
        return@lazy createInitialState()
    }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    val currentState: State
        get() = state.value

    init {
        subscribeEvents()
    }

    fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _state.value = newState
    }

    protected open fun parseArguments(savedStateHandle: SavedStateHandle) {
        //empty
    }

    protected fun <T> callApi(startState: State.() -> State, endState: State.() -> State, action: suspend () -> T, callback: (T) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { startState() }
            val response = action()
            setState { endState() }
            callback(response)
        }
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun createInitialState() : State
}