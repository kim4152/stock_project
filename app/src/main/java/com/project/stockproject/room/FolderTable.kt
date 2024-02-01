package com.project.stockproject.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "folder")
data class FolderTable (
    @PrimaryKey
    val folderName : String ?= "",

    val index : Int ?=0
)

@Entity(
    tableName = "item",
    foreignKeys = [ForeignKey(
        entity = FolderTable::class,
        parentColumns = ["folderName"],
        childColumns = ["folderName"],
        onDelete = ForeignKey.CASCADE   //부모가 지워지거나 업데이트 되면 자식도 실행
    )]
)
data class ItemTable(
    val itemName : String ?= "",
    val folderName: String?="",
)