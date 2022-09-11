package com.example.kristenmqttchatapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatListTable")
data class ChatlistEntity(@PrimaryKey(autoGenerate = true) val id : Long, val username : String)

@Entity(tableName = "ChatContactList")
data class ChatContactList(@PrimaryKey(autoGenerate = true) val id: Long, val ContactName : String, val Number : String)

@Entity(tableName = "MessageTable")
data class ChatEntity(@PrimaryKey(autoGenerate =  true) var id: Long, var sender :
String, val receiver : String, val chatId : String, var message: String, val status : Int, val messageId : String , val messageTime : String)








