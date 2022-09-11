package com.example.kristenmqttchatapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kristenmqttchatapp.R
import com.example.kristenmqttchatapp.chats.ChatsDirections
import com.example.kristenmqttchatapp.databinding.ChatlistlayoutBinding
import com.example.kristenmqttchatapp.room.ChatDatabase
import com.example.kristenmqttchatapp.room.ChatlistEntity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListAdapter( val context : Context) : ListAdapter<ChatlistEntity, ChatListAdapter.ChatListViewHolder>(
    ChatListDiffUtil()
) {
    val database = ChatDatabase.getDatabase(context)
    val uiScope = CoroutineScope(Dispatchers.Main)
    val activityContext = (context as AppCompatActivity)
    val sharedPreferences = activityContext.getSharedPreferences("user" , Context.MODE_PRIVATE)
    class ChatListViewHolder(holder : ChatlistlayoutBinding) : RecyclerView.ViewHolder(holder.root){
        val name = holder.chatName
        val recentMessage = holder.recentMessage
        val unreadMessagesCount = holder.unreadMessagesCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedlayout = ChatlistlayoutBinding.inflate(layoutInflater)
        return ChatListViewHolder(inflatedlayout)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val data = getItem(position)
        holder.name.text = data.username
        holder.recentMessage.text = getLastMessage()
        val username = sharedPreferences.getString("username" , "")
        holder.itemView.setOnClickListener {
            (context as AppCompatActivity).findNavController(R.id.nav_host_fragment)
                .navigate(ChatsDirections.actionChatsToChatPreview(data.username))
        }
        uiScope.launch {
           val lastmessage = database.databaseDao.selectPersonalChatLastMessage(data.username , sharedPreferences.getString("username" , "")!!)
            holder.recentMessage.text = lastmessage
           database.databaseDao.allUnreadMessages(data.username)
                .observe(this@ChatListAdapter.activityContext) {
                   if (it.equals(0)){
                       holder.unreadMessagesCount.visibility = View.GONE
                   }else{
                       holder.unreadMessagesCount.text = it.toString()
                   }
                }
        }

    }
    fun getLastMessage() : String{
        var lastMessage = ""
        val username = sharedPreferences.getString("username" , "")
        uiScope.launch {
            //val args : ChatPreviewArgs by (context as AppCompatActivity).navArgs()
            val message = database.databaseDao.getlastmessage("chidinmma" , username!!)
            lastMessage = message
        }
        return lastMessage
    }
}
class ChatListDiffUtil : DiffUtil.ItemCallback<ChatlistEntity>(){
    override fun areItemsTheSame(oldItem: ChatlistEntity, newItem: ChatlistEntity): Boolean {
        return  oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatlistEntity, newItem: ChatlistEntity): Boolean {
        return  oldItem == newItem
    }


}
