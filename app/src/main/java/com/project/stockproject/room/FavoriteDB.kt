package com.project.stockproject.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FolderTable::class,ItemTable::class], version = 2)
abstract class FavoriteDB:RoomDatabase() {
    abstract fun folderDAO() : FolderDAO
    abstract fun itemDAO() : ItemDAO

    companion object{
        val MIGRATION_1_2 : Migration = object :Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE item ADD COLUMN 'index' INTEGER NOT NULL DEFAULT 0")
            }

        }
    }

}