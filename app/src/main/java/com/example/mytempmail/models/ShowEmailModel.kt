package com.example.mytempmail.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShowEmailModel(

    @Expose
    @SerializedName("id")
    val id:Int?=null,

    @Expose
    @SerializedName("from")
    val from:String?=null,

    @Expose
    @SerializedName("subject")
    val subject:String?=null,

    @Expose
    @SerializedName("date")
    val date:String?=null,

    @Expose
    @SerializedName("attachments")
    val attachments:List<AttachmentItem>?=null,

    @Expose
    @SerializedName("body")
    val body:String?=null,

    @Expose
    @SerializedName("textBody")
    val textBody:String?=null,

    @Expose
    @SerializedName("htmlBody")
    val htmlBody:String?=null

)

data class AttachmentItem(

    @Expose
    @SerializedName("filename")
    val fileName:String?=null,

    @Expose
    @SerializedName("contentType")
    val contentType:String?=null,

    @Expose
    @SerializedName("size")
    val size:Int?=null

)
