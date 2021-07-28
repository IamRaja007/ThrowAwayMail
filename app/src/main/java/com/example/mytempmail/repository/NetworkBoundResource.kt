package com.example.mytempmail.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.mytempmail.models.MailBoxModel
import com.example.mytempmail.util.*
import com.example.mytempmail.util.Constants.Companion.TESTING_NETWORK_DELAY
import kotlinx.coroutines.*


abstract class NetworkBoundResource<ResponseObject,ViewStateType>(val wantToShowProgressBar:Boolean) { //we can make logic here as to what we want to do during a network request, like in this case we want to show progress bar

    protected val result=MediatorLiveData<DataState<ViewStateType>>() // protexcted beacuse we want this networkBoundResource file to be accessible within repository package

    init {
        result.value = DataState.loading(isLoading = wantToShowProgressBar) // here what we are doing is that whenever we do something with network, or make network request, we need to show progressbar

        GlobalScope.launch(Dispatchers.IO) {
            delay(TESTING_NETWORK_DELAY)

            withContext(Dispatchers.Main){
                val apiResponse=createCall()
                result.addSource(apiResponse){ response->
                    result.removeSource(apiResponse)
                    handleNetworkCall(response)
                }
            }

        }
    }

    private fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when(response){
            is ApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse ->{
                println("DEBUG: handleNetworkCall in NetworkBoundResource: ${response.errorMessage}")
                onReturnError(response.errorMessage)

            }

            is ApiEmptyResponse ->{
                println("DEBUG: handleNetworkCall in NetworkBoundResource: Returned Nothing")
                onReturnError("Http 204 error. Returned Nothing!")
            }
        }

    }

    fun onReturnError(message:String){
        result.value= DataState.error(message)
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>


    abstract fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
}