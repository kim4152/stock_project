package com.project.stockproject.favorite

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.stockproject.MainActivity
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.CustomDialogAddFavoriteBinding


class CustomDialogFavorite():DialogFragment() {
    private lateinit var binding: CustomDialogAddFavoriteBinding
    private lateinit var viewModel:MyViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // 여백을 투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= CustomDialogAddFavoriteBinding.inflate(layoutInflater,container,false)
        viewModel=ViewModelProviders.of(this)[MyViewModel::class.java]
        return binding.root
    }



    override fun onStart() {
        super.onStart()
        addFolder()//폴더 추가
        
    }

    //폴더 추가
    fun addFolder(){
        binding.addTextView.setOnClickListener {
            if(binding.subCons.isVisible){
                binding.subCons.visibility=View.INVISIBLE
            }else{
                binding.subCons.visibility=View.VISIBLE
            }
        }
    }

}