package com.example.mytempmail.ui.state

import retrofit2.http.Path

sealed class MainEventState {
    class GetGeneratedEmail() : MainEventState()

    class GetMailList(val username: String, val domain: String) : MainEventState()

    class GetShowSingleMessage(val username: String, val domain: String, val id: Int) :
        MainEventState()

    class None() : MainEventState()
}