package com.example.frooch.notification

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.frooch.MainActivity
import com.example.frooch.R
import com.example.frooch.databinding.FragmentNotificationBinding
import com.example.frooch.room.ChatDatabase
import com.example.frooch.room.NotificationEntity
import com.example.frooch.utils.NotificationAdapter
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Notification : Fragment() {
    private var menuItem : Menu? = null
    lateinit var binding : FragmentNotificationBinding
    lateinit var mContext : Context
    lateinit var room : ChatDatabase
    var uiScope = CoroutineScope(Dispatchers.Main)
    val notficationData : MutableList<NotificationEntity> =
        mutableListOf(
            NotificationEntity(1, "" , "Gallant liked your post on Chemistry" , false , 0 ,0) ,
            NotificationEntity(1, "" , "Gallant started following you", false , 0 ,0) ,
            NotificationEntity(1, "" , "Innocent commented your post on Chemistry", false, 0 ,0),
            NotificationEntity(1, "" , "Eric commented your post on Economics", false, 0 ,0)
            , NotificationEntity(1, "" , "Eric commented your post on Economics", false, 0 ,0) ,
            NotificationEntity(1, "" , "Eric commented your post on Economics", false, 0 ,0),
            NotificationEntity(1, "" , "BAF321 class has started", false, 0 ,0),
            NotificationEntity(1, "" , "Did you miss this?", false, 0 ,0),
            NotificationEntity(1, "" , "Did the lecturer come?", false, 0 ,0),
            NotificationEntity(1, "" , "Did you go?", false, 0 ,0),
            NotificationEntity(1, "" , "How did u go", false, 0 ,0),
            NotificationEntity(1, "" , "Did you fuck her?", false , 0 ,0),
            NotificationEntity(1, "" , "Did ", false, 0 ,0),
            NotificationEntity(1, "" , "Did you miss this?" , false , 0 ,0))
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        val viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        initDrawer()
        (mContext as MainActivity).notificaionFragmentIsActive = true
        setHasOptionsMenu(true)
        room = ChatDatabase.getDatabase(mContext)
        val adapter = NotificationAdapter(mContext , notficationData) {show -> showDeleteMenu(show)}
        binding.notificationRecyclerView.adapter = adapter
        uiScope.launch {
            room.databaseDao.getAllNotifications().observe(viewLifecycleOwner) { adapter.submitList(it)}
            room.databaseDao.updateSeenNotifications()
        }

        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuItem = menu
        inflater.inflate(R.menu.notification_menu , menu)
        val item = menu.findItem(R.id.topprofilebuttom)
        val view = MenuItemCompat.getActionView(item)
        showDeleteMenu(false)
        val profileimage : CircleImageView = view.findViewById(R.id.circleprofileimage)
        profileimage.setOnClickListener {
            val sharedPrefrnces : SharedPreferences = requireActivity().getSharedPreferences("user",
                AppCompatActivity.MODE_PRIVATE
            )
            val name = sharedPrefrnces.getString("username", "")

            if (name == ""){
                findNavController().navigate(NotificationDirections.actionNotificationToProfileOverview())

            }else{
                findNavController().navigate(NotificationDirections.actionNotificationToProfileOverview())
            }
        }
    }

    fun showDeleteMenu(show : Boolean){
        menuItem?.findItem(R.id.Delete)?.isVisible = show
    }
    fun initDrawer(){

        val activityObject = (mContext as AppCompatActivity)
        activityObject.setSupportActionBar(binding.toolbar)
        activityObject.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon = AppCompatResources.getDrawable(mContext, R.drawable.ic_baseline_menu_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mainActivityObject = (mContext as MainActivity)
        when(item.itemId){
            R.id.topprofilebuttom -> findNavController().navigate(NotificationDirections.actionNotificationToProfileOverview())
            android.R.id.home ->  {
                mainActivityObject.binding.myDrawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.Delete -> {
                delete()
            }
        }

        return true
    }
    private fun delete(){
        val adapter = NotificationAdapter(mContext , notficationData) {show -> showDeleteMenu(show)}
       val alertDialog = AlertDialog.Builder(mContext)
           alertDialog.setTitle("Delete")
           alertDialog.setMessage("Do you want to delete these Notification?")
           alertDialog.setPositiveButton("Delete") {_,_ ->
             adapter.deleteSelectedItems()
               showDeleteMenu(false)
           }
           alertDialog.setNegativeButton("Cancel"){_,_ ->
           }
        alertDialog.show()
    }

}