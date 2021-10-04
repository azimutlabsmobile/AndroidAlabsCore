package com.alabs.core_application.presentation.model

/**
 * Модель служит для валидации полей или чего либо
 * Обработка происходит исключительно во viewModel
 */
data class UIValidation(
    var type : String,
    var message : String
)