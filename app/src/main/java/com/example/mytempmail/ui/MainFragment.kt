package com.example.mytempmail.ui

import android.app.AlarmManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytempmail.DataStateListener
import com.example.mytempmail.R
import com.example.mytempmail.models.MailBoxModel
import com.example.mytempmail.models.ShowEmailModel
import com.example.mytempmail.ui.state.MainEventState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_showmail.*
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment(), RvMailsAdapter.Interaction {

    lateinit var viewModel: MainViewModel //This will be reference to viewModel that we instantiated in main Activity
    lateinit var dataStateHandler: DataStateListener
    lateinit var emailsListAdapter: RvMailsAdapter
    var tempEmail: String = ""
    var listMails: List<String>? = null
    lateinit var runnable: Runnable
    var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEmptyInbox.visibility = View.VISIBLE
        ivEmptyInbox.visibility = View.VISIBLE

        viewModel = activity?.let {
            ViewModelProvider(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")   //if activity is null throw this exception

        initRecyclerView()
        subscribeObservers()

        btnRegenerateEmail.setOnClickListener {
            triggerGetGeneratedMailEvent()
        }

        btncopy.setOnClickListener {
            val clipboard: ClipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipdata = ClipData.newPlainText("label", tempEmail)
            clipboard.setPrimaryClip(clipdata)
            Toast.makeText(requireContext(), "Email copied", Toast.LENGTH_SHORT).show()
        }
        lookForNewMessages()
    }

    private fun lookForNewMessages() {
        runnable = Runnable {
            triggerGetMailsEvent()
            handler.postDelayed(
                runnable,
                5000
            ) //every 5 sec, this handler will check for new messages in the inbox
        }
        handler.post(runnable)
    }

    override fun onStart() {
        super.onStart()
        triggerGetGeneratedMailEvent()
    }

    private fun setEmailFieldProperties(email: String) {
        tvGeneratedEmailShow.text = email
        listMails = SplitEmail(email)
    }

    private fun triggerGetGeneratedMailEvent() {
        viewModel.setStateEvent(MainEventState.GetGeneratedEmail())
    }

    private fun triggerGetMailsEvent() {
        listMails?.get(0)?.let { MainEventState.GetMailList(it, listMails?.get(1)!!) }?.let {
            viewModel.setStateEvent(
                it
            )
        }
    }

    private fun initRecyclerView() {
        rvShowMails.apply {
            layoutManager =
                LinearLayoutManager(this@MainFragment.context)// we wrote activity because we are in a fragment
            emailsListAdapter = RvMailsAdapter(this@MainFragment)
            adapter = emailsListAdapter
        }

    }


    private fun subscribeObservers() {  //we subscribe to all the observers that are in our viewModel. In this case we have two - dataState and eventState that we have to observe
        //Remember inside fragments, while using observers, use 'viewLifecycleOwner' and not 'this'
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->

            //Handling errors and Handling loading progress bar
            dataStateHandler.onDatStateChanged(dataState)

            //Handling Data
            dataState.data?.let { event ->  //if there is any incoming data then

                event.getContentIfNotHandled()?.let { mainViewState ->
                    mainViewState.email?.let { email ->
                        //set email in the editText email position
                        tempEmail = email
                        viewModel.setEmail(email)
//                        initCopyToClipboard()
                    }

                    mainViewState.emailsList?.let { mailLists ->
                        //set email lists to the inbox view
                        if (mailLists.isNotEmpty()) {
                            tvEmptyInbox.visibility = View.INVISIBLE
                            ivEmptyInbox.visibility = View.INVISIBLE
                        }
                        viewModel.setMailsListData(mailLists)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->

            viewState.emailsList?.let { emailslist ->
                //set email messages data
                emailsListAdapter.submitList(emailslist)
            }

            viewState.email?.let { mail ->
                //set email
                setEmailFieldProperties(mail)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must implement DataStateListener interface ")
        }
    }

    override fun onItemSelected(position: Int, item: MailBoxModel, itemID: Int) {
        val DOMAIN = listMails?.get(1)
        val LOGIN = listMails?.get(0)
        val ITEM_ID = itemID
        val fragment = MailFragment()
        val bundle = Bundle()
        bundle.putString("domain", DOMAIN)
        bundle.putString("login", LOGIN)
        bundle.putInt("itemID", ITEM_ID)
        fragment.arguments = bundle

        val fragmentTransactor = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransactor?.replace(R.id.fragment_container, fragment)?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        fun SplitEmail(string: String): List<String>? {
            val index = string.indexOf('@')
            val prefix = string.substring(0, index)
            val domain = string.substring(index + 1)
            val list: ArrayList<String> = ArrayList()
            list.add(0, prefix)
            list.add(1, domain)
            return list
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

}