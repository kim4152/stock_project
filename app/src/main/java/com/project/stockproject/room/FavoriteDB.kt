package com.project.stockproject.room

import androidx.room.RoomDatabase

abstract class FavoriteDB:RoomDatabase() {
    abstract fun folderDAO() : FolderDAO
    abstract fun itemDAO() : ItemDAO
}