package com.project.stockproject.favorite

import com.project.stockproject.room.FolderTable


data class CustomDialogItem(
    val folderName: String,
    var isChecked: Boolean,
    val count: String,
)
fun List<FolderTable>.transform():List<CustomDialogItem>{
    return this.map {
        CustomDialogItem(
            folderName = it.folderName,
            isChecked = false,
            count = "",
        )
    }
}
