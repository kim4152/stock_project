package com.project.stockproject.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDAO {
    @Insert
    fun insertItem(itemTable: ItemTable)
    @Query("SELECT COUNT(*) FROM item WHERE 'folderName' = :name")
    fun count(name: String) : Int
    @Query("SELECT * FROM item WHERE folderName= :name ORDER BY `index` ASC")
    fun getAll(name:String) :List<ItemTable>
}