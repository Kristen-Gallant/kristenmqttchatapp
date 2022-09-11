package com.example.kristenmqttchatapp.chats

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kristenmqttchatapp.utils.ChatListAdapter
import com.example.kristenmqttchatapp.databinding.FragmentChatsBinding
import com.example.kristenmqttchatapp.room.ChatDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient


class Chats : Fragment() {
    lateinit var binding : FragmentChatsBinding
    lateinit var mqttAndroidClient: MqttAndroidClient
    lateinit var mContext: Context
    var isAllFabsVisible : Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    var username : String = ""
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
        binding = FragmentChatsBinding.inflate(inflater)
        val viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences("user" , Context.MODE_PRIVATE)
        behaviourFAB()
        initAdapter()
        room = ChatDatabase.getDatabase(requireContext())
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        setHasOptionsMenu(true)

        return binding.root

    }
    fun behaviourFAB(){
        binding.logout.setVisibility(View.GONE);
        binding.startchat.setVisibility(View.GONE);
        binding.addAlarmActionText.setVisibility(View.GONE);
        binding.logouttext.setVisibility(View.GONE);
        binding.addFab.shrink()
        binding.addFab.setOnClickListener {
            isAllFabsVisible = if (!isAllFabsVisible) {
                binding.logout.show()
                binding.startchat.show()
                binding.addAlarmActionText.setVisibility(View.VISIBLE)
                binding.logouttext.setVisibility(View.VISIBLE)
                binding.addFab.extend()
                true
            } else {
                binding.logout.hide()
                binding.startchat.hide()
                binding.addAlarmActionText.setVisibility(View.GONE)
                binding.logouttext.setVisibility(View.GONE)
                binding.addFab.shrink()
                false
            }
        }
        binding.startchat.setOnClickListener {
            findNavController().navigate(ChatsDirections.actionChatsToNewChat())
        }
        binding.logout.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.commit()
                uiScope.launch {
                 room.databaseDao.emptyMessageTable()
                 room.databaseDao.emptyChatList()
                 requireActivity().finish()
                    startActivity(requireActivity().intent)
                }

            }
        }
    }
    fun initAdapter(){
        val viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        val adapter = ChatListAdapter(requireContext())
        binding.chatlistrecyclerview.adapter = adapter
       viewModel.data.observe(viewLifecycleOwner , Observer { data -> adapter.submitList(data) })
    }

}