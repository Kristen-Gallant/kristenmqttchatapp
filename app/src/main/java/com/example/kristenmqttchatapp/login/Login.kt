package com.example.kristenmqttchatapp.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kristenmqttchatapp.databinding.FragmentLoginBinding

class Login : Fragment() {
    lateinit var binding : FragmentLoginBinding
    lateinit var sharedPreferences: SharedPreferences
    companion object {
        fun newInstance() = Login()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        sharedPreferences = requireContext().getSharedPreferences("user" , Context.MODE_PRIVATE)
        if (sharedPreferences.getString("username" , "") == ""){
            //Do nothing
        }
       else{
           findNavController().navigate(LoginDirections.actionLoginToChats())

        }
        binding.loginbutoon.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("username" , binding.chosenUsername.text.toString())
            editor.apply()
            requireActivity().finish()
            startActivity(requireActivity().intent)


        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}