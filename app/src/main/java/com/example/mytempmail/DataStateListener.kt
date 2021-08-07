package com.example.mytempmail

import com.example.mytempmail.util.DataState

interface DataStateListener {
    fun onDatStateChanged(dataState: DataState<*>?)
}