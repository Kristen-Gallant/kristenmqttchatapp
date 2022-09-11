package com.example.kristenmqttchatapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kristenmqttchatapp.room.ChatContactList
import com.example.kristenmqttchatapp.room.ChatEntity
import com.example.kristenmqttchatapp.room.ChatlistEntity

@Dao
interface ChatsDao {
    @Insert()
    suspend fun insert(message : ChatEntity)

    @Query("Select * From messagetable")
    fun getAllChat(): LiveData<List<ChatEntity>>

    @Query("Select * From messagetable order by id desc")
    fun notifyChat(): LiveData<ChatEntity>

    @Query("Delete from messagetable")
    suspend fun deleteallchat()

    @Insert()
    suspend fun insertChat(chatList : ChatlistEntity)

    //Check if Chat Already Exist
    @Query("Select count() from chatlisttable where username = :key limit 1")
    suspend fun checkChat(key : String): Int
    //
    @Query("Delete from ChatListTable")
    suspend fun clearChatList()

    //Get All Chat List
    @Query("Select * from ChatListTable")
    fun getChatList() : LiveData<List<ChatlistEntity>>

    @Query("Select * from messagetable where sender = :sender and receiver = :user or sender=:user and receiver =:sender")
    fun selectPersonalChat(sender : String , user : String) : LiveData<List<ChatEntity>>


    //Received message
    @Query("Update messagetable set status = 2 where messageId =:key")
    suspend fun messageReceived(key : String)

    //Read message
    @Query("Update messagetable set status = 3 where messageId =:key")
    suspend fun messageRead(key : String)


    //Get Last Message to show in chat fragment
    @Query("Select message from messagetable where sender = :sender and receiver = :user or sender=:user and receiver =:sender " +
            "order by id ASC limit 1 ")
    suspend fun getlastmessage(sender: String , user: String) : String

    //get Private Chat of a person
    @Query("Select message from messagetable where sender = :sender and receiver = :user or sender=:user and receiver = :sender LIMIT 1")
    suspend fun selectPersonalChatLastMessage(sender : String , user : String) : String

    //Update message
    @Query("UPDATE messagetable SET status = 1 WHERE messageId = :messageId")
    suspend fun updateMessage(messageId : String)


    @Query("SELECT count() from messagetable where status = 0 and sender = :sender")
    fun allUnreadMessages(sender : String) : LiveData<Int>

    //get All Unread Messages Count
    @Query("SELECT count() from messagetable where status = 0 and sender = :sender")
    fun allUnreadMessagesCount(sender : String) : LiveData<List<Int>>

    //get All Message Id
    @Query("SELECT messageId from messagetable where status = 0 and sender = :sender")
    fun allUnreadMessagesId(sender : String) : LiveData<List<String>>

    //Unread Messages Count
    @Query("SELECT DISTINCT Count() FROM messagetable where receiver = :user and status = 0")
    fun getUnreadMessages(user : String) : LiveData<List<Int>>

    @Query("Select * from chatlisttable")
    fun testchat() : LiveData<List<ChatlistEntity>>
    //Empty Message Table
    @Query("DELETE FROM messagetable")
    suspend fun emptyMessageTable()

    //Empty Chatlist Table
    @Query("DELETE FROM chatlisttable")
    suspend fun emptyChatList()













}