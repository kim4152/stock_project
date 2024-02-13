package com.project.stockproject.room

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.project.stockproject.favorite.EditItem
import com.project.stockproject.stockInform.tabFragment.NewsItem
import com.project.stockproject.stockInform.tabFragment.NewsModel

@Entity(tableName = "folder")
data class FolderTable (
    @PrimaryKey
    @NonNull
    val folderName : String,

    val index : Int ?=0
)

fun List<FolderTable>.transform() : List<EditItem>{
    return this.map {
        EditItem(
            folderName = it.folderName ?: "",
            isSelected = false,
        )
    }
}



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
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    @NonNull
    val itemName : String,
    @NonNull
    val index : Int =0,
    val folderName: String?="",
    @NonNull
    val itemCode : String,
)