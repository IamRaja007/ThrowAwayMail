package com.example.mytempmail.ui

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.mytempmail.models.MailBoxModel
import com.example.mytempmail.models.ShowEmailModel
import com.example.mytempmail.repository.MainRepository
import com.example.mytempmail.ui.state.MainEventState
import com.example.mytempmail.ui.state.MainViewState
import com.example.mytempmail.util.AbsentLiveData
import com.example.mytempmail.util.DataState
import kotlinx.android.synthetic.main.fragment_main.*

class MainViewModel:ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()  //for displaying
    private val _eventState: MutableLiveData<MainEventState> = MutableLiveData() //for providing contents like get user and get blog posts

    val viewState: LiveData<MainViewState>
        get() = _viewState

    val dataState: LiveData<DataState<MainViewState>> = Transformations.switchMap(_eventState) { eventState ->
        eventState?.let {
            handleStateEvents(it)
        }
    }

    fun handleStateEvents(stateEvent: MainEventState): LiveData<DataState<MainViewState>> {

        return when (stateEvent) {
            is MainEventState.GetGeneratedEmail -> {
                return MainRepository.getGeneratedEmail()
            }
            is MainEventState.GetMailList -> {
                return MainRepository.getMailBoxMessages(stateEvent.username,stateEvent.domain)             //  getUser(stateEvent.userID)
            }

            is MainEventState.GetShowSingleMessage ->{
                return MainRepository.showSelectedMessage(stateEvent.username,stateEvent.domain,stateEvent.id)
            }

            is MainEventState.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun getCurrentViewStateOrNew(): MainViewState { //we need to get current view state to update it. we are writing 'orNew' because we will create a viewState when it is not present, that is for the first time case only

        return viewState.value?.let {
            it                //If we have a viewState, we assign it to 'value' variable
        } ?: MainViewState()   //If we dont have an viewstate, we create it

    }

    fun setMailsListData(emailsList:List<MailBoxModel>){   //if there are some new blog posts. Notice that we haven't made blogPosts parameter nullable(?) here
        val update=getCurrentViewStateOrNew()
        update.emailsList=emailsList
        _viewState.value=update

    }

    fun setEmail(email:String){
        val update=getCurrentViewStateOrNew()
        update.email=email
        _viewState.value=update
    }

    fun setSelectedMail(Show:ShowEmailModel){
        val update=getCurrentViewStateOrNew()
        update.showMail=Show
        _viewState.value=update
    }

    fun setStateEvent(event:MainEventState){   //we set the stateEvent to trigger the whole switchmap process
        _eventState.value=event
    }
}