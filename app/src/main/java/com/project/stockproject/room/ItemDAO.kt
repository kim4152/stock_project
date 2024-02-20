package com.project.stockproject.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDAO {
    @Insert
    fun insertItem(itemTable: ItemTable)
    @Query("SELECT COUNT(*) FROM item WHERE folderName = :name")
    fun count(name: String) : LiveData<Int>
    @Query("SELECT * FROM item WHERE folderName= :name ORDER BY `index` ASC")
    fun getAll(name:String) :List<ItemTable>
    @Query("SELECT COUNT(*) FROM item WHERE folderName = :fname AND itemName IN (:sname)")
    fun checkItem(fname: String, sname: List<String>): Int
    @Query("DELETE FROM item WHERE folderName = :fname AND  itemName IN(:iname)")
    fun itemDelete(fname: String, iname:List<String>)
    @Query("SELECT MAX(`id`) FROM item")
    fun getMaxOrder(): Int?

    @Query("SELECT `index` FROM item WHERE folderName = :fname AND itemName =:sname")
    fun getOrder(fname:String,sname:String): Int

    @Query("UPDATE item SET `index` = :index WHERE folderName = :fname AND itemName =:sname")
    fun updateOrder(fname: String,sname: String , index:Int)

}