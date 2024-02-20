package com.project.stockproject.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface FolderDAO {
    @Query("SELECT * FROM folder ORDER BY `index` ASC")
    fun getAll() :List<FolderTable>
    @Query("SELECT COUNT(*) FROM folder")
    fun count():Int
    @Query("SELECT COUNT(*) FROM folder WHERE folderName = :name")
    fun checkTextView(name: String):Int

    @Insert
    fun insertFolder(folderTable:FolderTable)
    @Query("DELETE FROM folder WHERE folderName IN(:name) ")
    fun folderDelete(name:List<String>)

    @Query("UPDATE folder SET folderName = :newName WHERE folderName = :oldName")
    fun updateFolderName(oldName: String, newName: String)
    @Query("SELECT MAX(`folderId`) FROM folder")
    fun getMaxOrder(): Int?
}