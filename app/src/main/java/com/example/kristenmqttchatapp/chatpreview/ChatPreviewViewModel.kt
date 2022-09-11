package com.example.frooch.chatpreview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kristenmqttchatapp.room.ChatDatabase

class ChatPreviewViewModel(val app : Application) : AndroidViewModel(app) {
    //val args : ChatPreviewArgs by app.applicationContextnavArgs()
    var roomDatabase = ChatDatabase.getDatabase(app.applicationContext)
    //val data = roomDatabase.chatsDao.getallchat()

    init {

    }



}