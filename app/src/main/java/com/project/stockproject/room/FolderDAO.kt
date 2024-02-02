package com.project.stockproject.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface FolderDAO {
    @Query("SELECT * FROM folder ORDER BY `index` DESC")
    fun getAll() :List<FolderTable>
    @Query("SELECT COUNT(*) FROM folder")
    fun count():Int
    @Query("SELECT COUNT(*) FROM folder WHERE folderName = :name")
    fun checkTextView(name: String):Int

    @Insert
    fun insertFolder(folderTable:FolderTable)
    @Query("DELETE FROM folder WHERE folderName = :name")
    fun folderDelete(name:String)

}