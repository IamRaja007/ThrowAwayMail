package com.example.mytempmail.repository

import androidx.lifecycle.LiveData
import com.example.mytempmail.api.RetrofitClient
import com.example.mytempmail.models.MailBoxModel
import com.example.mytempmail.models.ShowEmailModel
import com.example.mytempmail.ui.state.MainViewState
import com.example.mytempmail.util.ApiSuccessResponse
import com.example.mytempmail.util.DataState
import com.example.mytempmail.util.GenericApiResponse
import com.google.gson.Gson

object MainRepository {

    fun getGeneratedEmail():LiveData<DataState<MainViewState>>{
        return object: NetworkBoundResource<List<String>,MainViewState>(true){
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<List<String>>) {
                result.value=DataState.data(
                    data= MainViewState(email = response.body[0])
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<List<String>>> {
                return RetrofitClient.apiService.generateEmail()
            }

        }.asLiveData()
    }

    fun getMailBoxMessages(username:String,domain:String):LiveData<DataState<MainViewState>>{

        return object: NetworkBoundResource<List<MailBoxModel>,MainViewState>(false){

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<List<MailBoxModel>>) {
                result.value=DataState.data(
                    data= MainViewState(emailsList = response.body)
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<List<MailBoxModel>>> {
                return RetrofitClient.apiService.getMailMessages(username, domain)
            }

        }.asLiveData()
    }

    fun showSelectedMessage(username:String,domain:String,id:Int):LiveData<DataState<MainViewState>>{
        return object: NetworkBoundResource<ShowEmailModel,MainViewState>(true){
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<ShowEmailModel>) {
                result.value=DataState.data(
                    data= MainViewState(showMail =response.body)
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ShowEmailModel>> {
                return RetrofitClient.apiService.getShowSelectedMessage(username, domain, id)
            }

        }.asLiveData()
    }
}