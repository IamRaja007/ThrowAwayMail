package com.example.mytempmail.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MailBoxModel(
    @Expose
    @SerializedName("id")
    val id: String? = null,

    @Expose
    @SerializedName("from")
    val from: String? = null,

    @Expose
    @SerializedName("subject")
    val subject: String? = null,

    @Expose
    @SerializedName("date")
    val date: String? = null,
)