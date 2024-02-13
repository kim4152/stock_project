package com.project.stockproject.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FolderTable::class,ItemTable::class], version = 3)
abstract class FavoriteDB:RoomDatabase() {
    abstract fun folderDAO() : FolderDAO
    abstract fun itemDAO() : ItemDAO

    companion object{
        val MIGRATION_2_3 : Migration = object :Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE item ADD COLUMN 'itemCode' TEXT NOT NULL")
            }

        }
    }

}