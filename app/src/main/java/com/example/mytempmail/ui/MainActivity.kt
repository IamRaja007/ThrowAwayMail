package com.example.mytempmail.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytempmail.DataStateListener
import com.example.mytempmail.R
import com.example.mytempmail.util.DataState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.ClassCastException

class MainActivity : AppCompatActivity(),DataStateListener {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel= ViewModelProvider(this).get(MainViewModel::class.java)

        showMainFragment()
    }

    private fun showMainFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MainFragment(),"MainFragment").commit()
    }

    override fun onDatStateChanged(dataState: DataState<*>?) {
        handleDataStateChanged(dataState)
    }

    private fun handleDataStateChanged(dataState: DataState<*>?) {

        dataState?.let {
            //Handle loading
            showProgressBar(it.loading)

            //Handle error message
            it.message?.let {event ->
                event.getContentIfNotHandled()?.let {message->
                    showToast(message)
                }
            }
        }

    }

    private fun showToast(message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
    private fun showProgressBar(isVisible:Boolean){
        if(isVisible){
            progress_bar.visibility= View.VISIBLE
        }
        else{
            progress_bar.visibility= View.GONE
        }
    }
}
