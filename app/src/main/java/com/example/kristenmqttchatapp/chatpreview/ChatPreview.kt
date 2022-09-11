package com.example.kristenmqttchatapp.chatpreview

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frooch.chatpreview.ChatPreviewViewModel
import com.example.kristenmqttchatapp.utils.ChatPreviewRecyclerView
import com.example.kristenmqttchatapp.MainActivity
import com.example.kristenmqttchatapp.chats.Chats
import com.example.kristenmqttchatapp.databinding.FragmentChatPreviewBinding
import com.example.kristenmqttchatapp.room.ChatDatabase
import com.example.kristenmqttchatapp.room.ChatEntity
import com.example.kristenmqttchatapp.room.ChatlistEntity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*


class ChatPreview : Fragment() {
    lateinit var binding : FragmentChatPreviewBinding
    lateinit var mContext: Context
    lateinit var sharedPreferences: SharedPreferences
    private val uiScope = CoroutineScope(Dispatchers.Main)
    lateinit var room : ChatDatabase
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentChatPreviewBinding.inflate(inflater)
        val viewModel = ViewModelProvider(this).get(ChatPreviewViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences("user" , Context.MODE_PRIVATE)
        room = ChatDatabase.getDatabase(requireContext())
        clickListeners()
        initAdapter()

        return binding.root

    }

    private fun initAdapter() {
        val activityObject = (mContext as MainActivity)
        val args : ChatPreviewArgs by navArgs()
        activityObject.chatId = args.chatId
        val username = sharedPreferences.getString("username" , "").toString()
        val adapter = ChatPreviewRecyclerView(requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.chatpreviewrecyclerview.itemAnimator = null
        layoutManager.stackFromEnd = true
        binding.chatpreviewrecyclerview.adapter = adapter
        binding.chatpreviewrecyclerview.layoutManager = layoutManager
        val adapterData = room.databaseDao.selectPersonalChat(args.chatId , username )
        adapterData.observe(viewLifecycleOwner , Observer { adapter.submitList(it)})
    }

    fun clickListeners(){
        val activityObject = (mContext as MainActivity)
        val args : ChatPreviewArgs by navArgs()
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendmessage.setOnClickListener {

                publish(binding.typingSpace.text.toString())
                binding.typingSpace.text.clear()

        }
        binding.receiverName.text = args.chatId
    }

    fun publish(message : String){
        val username = sharedPreferences.getString("username" , "")
        val simpleDateFormat =  SimpleDateFormat("hh:mm",
            Locale.getDefault())
        val args : ChatPreviewArgs by navArgs()
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        val chatusername = sharedPreferences.getString("chatusername" , "")
        val topic = "${args.chatId}/$chatusername"
        val randomString = UUID.randomUUID().toString().substring(0,5)
        val edittexttext = message
        val payload = "$username:$chatusername&$randomString#$currentDateAndTime!$edittexttext"
        val message1 = payload.substringAfter("!")
        var encodedPayload = ByteArray(0)
        val entity = ChatEntity(0 , username!! , chatId = chatusername!! , message = message1  , receiver = args!!.chatId ,
            status = 0 , messageId = randomString , messageTime = currentDateAndTime
        )
        uiScope.launch {
            val checkChat = room.databaseDao.checkChat(args.chatId)
            if (checkChat == 0){
                val chatListEntity = ChatlistEntity(0 , args.chatId)
                room.databaseDao.insertChat(chatListEntity)
                room.databaseDao.insert(entity)
            }else{
                room.databaseDao.insert(entity)
            }
        }
        try {

            encodedPayload = payload.toByteArray(charset("UTF-8"))
            val message2 = MqttMessage(encodedPayload)
            message2.isRetained = false
            message2.qos = 2
            (mContext as MainActivity).mqttAndroidClient.publish(topic, message2 , null , object : IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?){

                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

                }

            })
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}