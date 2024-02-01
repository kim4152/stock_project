package com.project.stockproject.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface FolderDAO {
    @Query("SELECT * FROM folder")
    fun getAll() :List<FolderTable>
    @Query("SELECT COUNT(*) FROM folder")
    fun count():Int

    @Insert
    fun insertFolder(folderTable:FolderTable)

}