package com.example.mytempmail.ui

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mytempmail.DataStateListener
import com.example.mytempmail.R
import com.example.mytempmail.models.AttachmentItem
import com.example.mytempmail.models.ShowEmailModel
import com.example.mytempmail.ui.state.MainEventState
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_showmail.*
import java.io.File
import java.net.URL

const val Domain="domain"
const val Login="login"
const val Item_ID="itemID"

class MailFragment:Fragment() {
    private var domain:String?=null
    private var login:String?=null
    private var item_Id:Int?=null
    lateinit var viewModel: MainViewModel
    lateinit var dataStateHandler: DataStateListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        domain=arguments?.getString(Domain)
        login=arguments?.getString(Login)
        item_Id=arguments?.getInt(Item_ID)
        println("printing: $item_Id")

        return inflater.inflate(R.layout.fragment_showmail,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("Special: trigger get generated mail event $login $domain ")
        viewModel=activity?.let {
            ViewModelProvider(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")   //if activity is null throw this exception

        subscribeObservers()
        triggerShowMail()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            dataStateHandler=context as DataStateListener
        }catch (e: ClassCastException){
            println("DEBUG: $context must implement DataStateListener interface ")
        }
    }

    private fun triggerShowMail() {
        viewModel.setStateEvent(MainEventState.GetShowSingleMessage(login!!,domain!!,item_Id!!))
        println("DEBUG: trigger get generated mail event${MainEventState.GetShowSingleMessage(login!!,domain!!,item_Id!!).username}")
    }


    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            println("DEBUG: dataState : {$dataState}")

            //Handling errors and Handling loading progress bar
            dataStateHandler.onDatStateChanged(dataState)

            //Handling Data
            dataState.data?.let { event ->  //if there is any incoming data then

                event.getContentIfNotHandled()?.let { mainViewState ->
                    mainViewState.showMail?.let {
                        println("LETSCHECK: ${it.id}")
                        viewModel.setSelectedMail(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner,  { viewState ->

            viewState.showMail?.let {
                println("CHECKK: $it.toString()")
                setShowMailView(it)
            }

        })
    }

    private fun setShowMailView(mail: ShowEmailModel){

        val info = MainFragment.SplitEmail(mail.from!!)?.get(0)!! //to get the email address part before @ character
        cl.visibility=View.VISIBLE
        tvDate.text=mail.date
        tvSubject.text=mail.subject
        webview.loadDataWithBaseURL(null, mail.body!!, "text/html", "UTF-8", null)
        tvName.text=info
        Picasso.get().load(
            "https://ui-avatars.com/api/?background=234&color=fff&size=256&rounded=true&name=$info"
        ).placeholder(R.drawable.demo_image).into(circleImageView)

        if(mail.attachments?.isNotEmpty()!!){
            setDownloadContents(mail.attachments)
        }

    }

    private fun setDownloadContents(attachments: List<AttachmentItem>) {

            println("LETSCHECKK"+attachments[0].fileName)
            val url =
                "https://www.1secmail.com/api/v1/?action=download&login=" + login + "&domain=" + domain + "&id=" + item_Id + "&file=" + attachments[0].fileName
            layoutDownload.visibility=View.VISIBLE
            tvAttachmentName.text= attachments[0].fileName
            val fileSize=(attachments[0].size!!).toString() + "Kb"
            tvSize.text=fileSize
            println("SETPO: AMi achi")

            layoutDownload.setOnClickListener {
                println("SETPO: AMi esechi")
                println("printing: $attachments[0]")
                println("printing: $attachments[0].fileName")

                downloadFile(url, attachments[0].fileName!!,attachments[0].contentType!!)
            }
        }



    private fun downloadFile(url: String, fileName: String,contentType:String) {
        Toast.makeText(requireContext(), "Downloading ...", Toast.LENGTH_SHORT).show()
        println("printing: filename $fileName")
        println("printing : url $url")

        val request = DownloadManager.Request(Uri.parse(url))

        request.apply {
            setTitle(fileName)
            setMimeType(contentType)
            setAllowedOverMetered(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MailDownloads/$fileName")
        }

        val manager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

}
