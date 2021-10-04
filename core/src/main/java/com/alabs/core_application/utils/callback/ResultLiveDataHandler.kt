package com.alabs.core_application.utils.callback

/**
 * Получаем базовые события с LiveData для отображения загрузчика и вывода ошибок
 */
interface ResultLiveDataHandler {

    fun showLoader(){
        // do nothing
    }

    fun hideLoader(){
        // do nothing
    }

    fun showPullToRefreshLoader(){
        // do nothing
    }

    fun hidePullToRefreshLoader(){
        // do nothing
    }

    fun showPagingLoader(){

    }

    fun hidePagingLoader(){

    }

    fun error(msg : String){
        // do nothing
    }

    fun errorByType(type : String, msg : String){
        // do nothing
    }

    fun success(){
        // do nothing
    }

    fun successMessage(msg: String) {
        // do nothing
    }

    fun showLoaderByType(type: String){

    }

    fun hideLoaderByType(type: String){

    }

    fun `on500Error`(error : String){

    }

    fun `on404Error`(error : String){

    }


}