package com.example.kristenmqttchatapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.example.kristenmqttchatapp.databinding.ActivityMainBinding
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
const val CHANNEL_ID = "Chat Notification"
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var chatId : String? = null
    var username = ""
    lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var room : ChatDatabase
    private val uiScope = CoroutineScope(Dispatchers.Main)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this , R.layout.activity_main)
        room = ChatDatabase.getDatabase(this)
        sharedPreferences = getSharedPreferences("user" , MODE_PRIVATE)
        connect(applicationContext)
        createNotificationChannel()
    }


    fun subscribe(){
        val chatusername = sharedPreferences.getString("username" , "")
        val topic = "$chatusername/#"
        val qos = 2
        try {
            val subToken: IMqttToken = mqttAndroidClient.subscribe(topic, qos)
            subToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Snackbar.make(findViewById(android.R.id.content) , "Subscribed" , Snackbar.LENGTH_SHORT).show()
                }
                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Snackbar.make(findViewById(android.R.id.content) , "Didn't Subscribe" , Snackbar.LENGTH_SHORT).show()
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }
    fun connect(applicationContext : Context) {
        try {
            val username = sharedPreferences.getString("username" , "").toString()
            mqttAndroidClient = MqttAndroidClient (applicationContext,"tcp://broker.hivemq.com:1883",
                username
            )
            val options = MqttConnectOptions()
            options.isCleanSession = false
            options.isAutomaticReconnect = true
            val token = mqttAndroidClient.connect(options)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken)  {
                    Log.i("Connection", "success ")
                    subscribe()
                    receiveMessages()
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.i("Connection", "failure")
                    Toast.makeText(applicationContext , "  ${exception.message}" , Toast.LENGTH_LONG).show()
                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException){
            // Give your callback on connection failure here
            e.printStackTrace()
            Toast.makeText(applicationContext , "${e.message}" , Toast.LENGTH_LONG).show()
        }
    }


    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID , "Chat" , NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Chats"
                enableVibration(true)
            }
            val nManger : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManger.createNotificationChannel(channel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("Alarm Notification" , "Reminder for class" , NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Reminder"
                enableVibration(true)
            }
            val nManger : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManger.createNotificationChannel(channel)

        }
    }

    fun receiveMessages(){
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                Log.i("Connection Lost", "${cause.message}")
            }
            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    val data = String(message.payload, charset("UTF-8"))
                    val me = sharedPreferences.getString("username" , "")!!
                    val senderusername = data.substringBefore(":")
                    uiScope.launch {
                        val chatname = data.substringBefore(":")
                        val checkChat = room.databaseDao.checkChat(chatname)
                        if (checkChat > 0){
                            //
                        }else{
                            uiScope.launch {
                                when(chatname){
                                    "status" ->{

                                    }
                                    "gotten" ->{

                                    }
                                    else -> {
                                        val chatId = data.substringAfter(":").substringBefore("&")
                                        val chatListEntity = ChatlistEntity(0 , chatname )
                                        room.databaseDao.insertChat(chatListEntity)
                                    }
                                }

                            }
                        }
                        when(data.substringBefore(":")){
                            else -> {
                                val messageId = data.substringAfter("&").substringBefore("#")
                                val chatId = data.substringAfter(":").substringBefore("&")
                                val personalMessage = data.substringAfter("!")
                                val time = data.substringAfter("#").substringBefore("!")
                                val entity = ChatEntity(0 , chatname, me,chatId ,   message = personalMessage , 0 , messageId = messageId , messageTime = time)
                                room.databaseDao.insert(entity)

                                val notificationBuilder = NotificationCompat.Builder(this@MainActivity , CHANNEL_ID )
                                    .setContentTitle(entity.sender)
                                    .setContentText(entity.message.substringAfter("#"))
                                    .setSmallIcon(androidx.media.R.drawable.notification_bg)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .build()
                                val notificationManager = NotificationManagerCompat.from(this@MainActivity)
                                notificationManager.notify(1 , notificationBuilder)

                            }
                        }
                    }

                } catch (e: Exception) {

                }
            }
            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

    }







}