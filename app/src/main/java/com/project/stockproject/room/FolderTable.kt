package com.project.stockproject.room

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.project.stockproject.favorite.editFolder.EditItem

@Entity(
    tableName = "folder",
    indices = [Index(value = ["folderName"], unique = true)]
)
data class FolderTable(
    @PrimaryKey(autoGenerate = true)
    val folderId: Int = 0,

    @NonNull
    val folderName: String,

    val index: Int? = folderId
)

@Entity(
    tableName = "item",
    foreignKeys = [ForeignKey(
        entity = FolderTable::class,
        parentColumns = ["folderName"],
        childColumns = ["folderName"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
        // 부모가 지워지거나 업데이트 되면 자식도 실행
    )]
)
data class ItemTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @NonNull
    val itemName: String,


    val index: Int? = id,
    @NonNull
    val folderName: String,

    @NonNull
    val itemCode: String
)
fun List<FolderTable>.transform() : List<EditItem>{
    return this.map {
        EditItem(
            folderName = it.folderName ?: "",
            isSelected = false,
        )
    }
}
