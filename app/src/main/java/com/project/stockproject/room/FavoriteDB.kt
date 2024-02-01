package com.project.stockproject.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FolderTable::class,ItemTable::class], version = 1)
abstract class FavoriteDB:RoomDatabase() {
    abstract fun folderDAO() : FolderDAO
    abstract fun itemDAO() : ItemDAO



}