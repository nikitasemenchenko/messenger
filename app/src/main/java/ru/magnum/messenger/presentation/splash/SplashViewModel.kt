package ru.magnum.messenger.presentation.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.magnum.messenger.domain.usecase.CheckAuthUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAuthUseCase: CheckAuthUseCase
): ViewModel() {
    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state = _state.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth(){
        val user = checkAuthUseCase()
        _state.value =  if(user != null) SplashState.Authorized else SplashState.Unauthorized
    }
}