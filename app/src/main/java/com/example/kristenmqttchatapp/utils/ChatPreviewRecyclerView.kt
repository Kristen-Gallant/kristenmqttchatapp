package com.example.kristenmqttchatapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kristenmqttchatapp.R
import com.example.kristenmqttchatapp.databinding.ReceiverListitemBinding
import com.example.kristenmqttchatapp.databinding.SenderListitemBinding
import com.example.kristenmqttchatapp.room.ChatEntity


class ChatPreviewRecyclerView(val context: Context) : ListAdapter<ChatEntity, RecyclerView.ViewHolder >(
    Diffutil()
) {
    val itemSelected = mutableListOf<Int>()
    var isenabled = false
    var activityasintext = (context as AppCompatActivity)
    val SP = activityasintext.getSharedPreferences("user" , Context.MODE_PRIVATE)
    class IncomingMessageViewHolder(holder : ReceiverListitemBinding) : RecyclerView.ViewHolder(holder.root){
        val message = holder.incomingMsg
        val time = holder.incomingMsgTime

    }
    class OutGoingMessageViewHolder(holder : SenderListitemBinding) : RecyclerView.ViewHolder(holder.root){
        val message = holder.outgoingMsg
        val check = holder.check
        val status = holder.status
        val time = holder.outgoingMsgTime

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val inflater = LayoutInflater.from(parent.context)
            val layout = SenderListitemBinding.inflate(inflater)
            return OutGoingMessageViewHolder(layout)
        }else{
            val inflater = LayoutInflater.from(parent.context)
            val layout = ReceiverListitemBinding.inflate(inflater)
            return IncomingMessageViewHolder(layout)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (getItem(position).sender == SP.getString("username" , "")){
            val outgoingmessagelayout = holder as? OutGoingMessageViewHolder
            outgoingmessagelayout?.message?.text = data.message
            outgoingmessagelayout?.time?.text = data.messageTime

        }else{
            val incomingMessageViewHolder = holder as? IncomingMessageViewHolder
            incomingMessageViewHolder?.message?.text = data.message
            incomingMessageViewHolder?.time?.text = data.messageTime
        }

    }

    private fun selecteditem(outgoingmessagelayout: OutGoingMessageViewHolder?, data: ChatEntity?, position: Int) {
        isenabled = true
        itemSelected.add(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).sender == SP.getString("username" , "")){
            return 1
        }else{
            return super.getItemViewType(position)
        }
    }
}
class Diffutil: DiffUtil.ItemCallback<ChatEntity>(){

    override fun areContentsTheSame(oldItem: ChatEntity, newItem: ChatEntity): Boolean {
        return oldItem == newItem

    }

    override fun areItemsTheSame(oldItem: ChatEntity, newItem: ChatEntity): Boolean {
        return oldItem == newItem
    }


}