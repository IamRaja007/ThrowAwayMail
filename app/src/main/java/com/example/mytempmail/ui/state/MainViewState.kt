package com.example.mytempmail.ui.state

import com.example.mytempmail.models.MailBoxModel
import com.example.mytempmail.models.ShowEmailModel

data class MainViewState(  //Acts like a wrapper class.It packages everything(all data models) that will be in the view(activity). Here there will be only one Mutable live data object which will contain all these objects( users and list of blogposts )
    var email:String?=null,
    var emailsList:List<MailBoxModel>?=null,
    var showMail: ShowEmailModel?=null
) {
   fun getTheEmail():String?{
       return email
   }
}