package com.example.kristenmqttchatapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatEntity::class ,   ChatlistEntity::class ,
    ChatContactList::class ] , exportSchema = false ,
    version = 9)
abstract class ChatDatabase() : RoomDatabase() {
    abstract val databaseDao : ChatsDao
    companion object{
        @Volatile
          var instance : ChatDatabase? = null
        fun getDatabase(context: Context): ChatDatabase {
            if (instance == null){
                synchronized(this){
                    instance = Room.databaseBuilder(context,
                        ChatDatabase::class.java,
                        "ChatDB")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance!!
        }


    }
}