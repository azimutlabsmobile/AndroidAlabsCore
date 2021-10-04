package com.alabs.KDispatcher


/**
 *  Правила по использованию IKDispatcher
 *  1. Создание константы события должно лежать в data->сonstants EventConstant за исключением global
 *  2. Создание событий дожно находиться в пакете конкретноой фитчи если оно испольпользуеться в другом модуле
 *  3. Если событие требуется использовать в другом модуле создаем константу в EventConstant в global
 *  4. Название СОБЫТИЕ_РАЗДЕЛ(название фитчи)_EVENT (REFRESH_TRANSFER_EVENT) если в глобальном модуле СОБЫТИЕ(что делаем)_GLOBAL_EVENT
 *  НУЖНО ОБЯЗАТЕЛЬНО ОЧИЩАТЬ РЕСУРСЫ!!!
 */
object KDispatcherEventConstant {

    /**
     * При успешной авторизации
     */
    const val SUCCESS_AUTH_CORE_EVENT = "SUCCESS_AUTH_CORE_EVENT"
}