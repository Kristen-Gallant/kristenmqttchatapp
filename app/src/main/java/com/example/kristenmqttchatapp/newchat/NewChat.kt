package com.example.kristenmqttchatapp.newchat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kristenmqttchatapp.R
import com.example.kristenmqttchatapp.databinding.FragmentNewChatBinding
import com.example.kristenmqttchatapp.room.ChatDatabase
import com.example.kristenmqttchatapp.room.ChatlistEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewChat : Fragment() {
   lateinit var binding : FragmentNewChatBinding
   lateinit var room : ChatDatabase
    companion object {
        fun newInstance() = NewChat()
    }

    private lateinit var viewModel: NewChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewChatBinding.inflate(inflater)
        room = ChatDatabase.getDatabase(requireContext())
        binding.startchat.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                val newChat = ChatlistEntity(0 , binding.receiverName.text.toString())
                room.databaseDao.insertChat(newChat)
                findNavController().navigate(NewChatDirections.
                actionNewChatToChatPreview(binding.receiverName.text.toString()))
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewChatViewModel::class.java)


    }

}