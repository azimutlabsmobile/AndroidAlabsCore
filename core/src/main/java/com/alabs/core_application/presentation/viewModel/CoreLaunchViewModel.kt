package com.alabs.core_application.presentation.viewModel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.alabs.KDispatcher.IKDispatcher
import com.alabs.core_application.data.network.Status
import com.alabs.core_application.presentation.model.UIValidation
import com.alabs.core_application.utils.delegates.CoreCoroutine
import com.alabs.core_application.utils.delegates.CoreCoroutineDelegate
import com.alabs.core_application.utils.wrappers.EventWrapper

abstract class CoreLaunchViewModel(vararg useCases: CoreCoroutine) : ViewModel(), CoreCoroutine by CoreCoroutineDelegate(),
    IKDispatcher {

    private var listUseCase: Array<out CoreCoroutine> = arrayOf()


    val errorLiveData: LiveData<EventWrapper<String>> =
        MediatorLiveData<EventWrapper<String>>().apply {
            useCases.forEach {
                addSource(it.errorEventLiveData) { valueLiveDataUseCase ->
                    value = valueLiveDataUseCase
                }
            }

            addSource(errorEventLiveData) { valueLiveDataViewModel ->
                value = valueLiveDataViewModel
            }
        }

    val errorByTypeLiveData: LiveData<EventWrapper<UIValidation>> =
        MediatorLiveData<EventWrapper<UIValidation>>().apply {
            useCases.forEach {
                addSource(it.errorEventByTypeLiveData) { valueLiveDataUseCase ->
                    value = valueLiveDataUseCase
                }
            }

            addSource(errorEventByTypeLiveData) { valueLiveDataViewModel ->
                value = valueLiveDataViewModel
            }
        }

    val errorByCodeLiveData: LiveData<EventWrapper<Pair<Int, String>>> =
        MediatorLiveData<EventWrapper<Pair<Int, String>>>().apply {
            useCases.forEach {
                addSource(it.errorEventByCodeLiveData) { valueLiveDataUseCase ->
                    value = valueLiveDataUseCase
                }
            }

            addSource(errorEventByCodeLiveData) { valueLiveDataViewModel ->
                value = valueLiveDataViewModel
            }
        }

    val successMessageLiveData: LiveData<EventWrapper<String>> =
        MediatorLiveData<EventWrapper<String>>().apply {
            useCases.forEach {
                addSource(it.successMessageEventLiveData) { valueLiveDataUseCase ->
                    value = valueLiveDataUseCase
                }
            }

            addSource(successMessageEventLiveData) { valueLiveDataViewModel ->
                value = valueLiveDataViewModel
            }
        }

    val statusLiveData: LiveData<EventWrapper<Status>> = MediatorLiveData<EventWrapper<Status>>().apply {
        useCases.forEach {
            addSource(it.statusEventLiveData) { valueLiveDataUseCase ->
                value = valueLiveDataUseCase
            }
        }

        addSource(statusEventLiveData) { valueLiveDataViewModel ->
            value = valueLiveDataViewModel
        }
    }

    val redirectFragment: LiveData<EventWrapper<Pair<Int, Bundle?>>> =
        MediatorLiveData<EventWrapper<Pair<Int, Bundle?>>>().apply {
            useCases.forEach {
                addSource(it.redirectEventFragment) { valueLiveDataUseCase ->
                    value = valueLiveDataUseCase
                }
            }

            addSource(redirectEventFragment) { valueLiveDataViewModel ->
                value = valueLiveDataViewModel
            }
        }


    val loadingByTypeLiveData: LiveData<EventWrapper<Pair<String, Boolean>>> = MediatorLiveData<EventWrapper<Pair<String, Boolean>>>().apply {
        useCases.forEach {
            addSource(it.loadingByTypeEvent) { valueLiveDataUseCase ->
                value = valueLiveDataUseCase
            }
        }

        addSource(loadingByTypeEvent) { valueLiveDataViewModel ->
            value = valueLiveDataViewModel
        }
    }

    init {
        this.listUseCase = useCases
    }

    override fun onCleared() {
        super.onCleared()
        listUseCase.forEach {
            it.clearCoroutine()
        }
    }
}