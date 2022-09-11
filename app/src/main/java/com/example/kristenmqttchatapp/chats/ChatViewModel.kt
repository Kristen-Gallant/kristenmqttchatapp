package com.example.kristenmqttchatapp.chats

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.kristenmqttchatapp.room.ChatDatabase
import com.example.kristenmqttchatapp.room.ChatEntity


class ChatViewModel(app : Application) : AndroidViewModel(app) {
    //val data = MutableLiveData<List<ChatEntity>>()
    var _data = MutableLiveData<List<ChatEntity>>()
    var roomDatabase = ChatDatabase.getDatabase(app.applicationContext)
    val database = roomDatabase.databaseDao.getAllChat()
    val sharedPreferences = getApplication<Application>().getSharedPreferences("user" ,
        Context.MODE_PRIVATE
    )
    val username = sharedPreferences.getString("username" , "")
    val data = roomDatabase.databaseDao.getChatList()

    init {
//        viewModelScope.launch {
//            getchats()
//
//        }



    }
//    suspend fun getchats(){
//        data = roomDatabase.chatsDao.getallchat()
//
//    }



}