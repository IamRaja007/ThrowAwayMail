package com.example.mytempmail.api

import androidx.lifecycle.LiveData
import com.example.mytempmail.models.MailBoxModel
import com.example.mytempmail.models.ShowEmailModel
import com.example.mytempmail.util.GenericApiResponse
import retrofit2.http.*


interface ApiService {
    @GET("?action=genRandomMailbox&count=1")
    fun generateEmail(): LiveData<GenericApiResponse<List<String>>>

    @GET("?action=getMessages")
    fun getMailMessages(
        @Query("login") username: String,
        @Query("domain") domain: String
    ): LiveData<GenericApiResponse<List<MailBoxModel>>>  //every request to network with retrofit will give us live data

    @GET("?action=readMessage")
    fun getShowSelectedMessage(
        @Query("login") username: String,
        @Query("domain") domain: String,
        @Query("id") id: Int
    ): LiveData<GenericApiResponse<ShowEmailModel>>
}