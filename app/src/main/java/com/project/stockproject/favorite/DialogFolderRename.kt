package com.project.stockproject.favorite

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.project.stockproject.MainActivity
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.databinding.FragmentDialogFolderRenameBinding

class DialogFolderRename() : DialogFragment() {
    private lateinit var binding: FragmentDialogFolderRenameBinding
    private lateinit var viewModel:MyViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // 여백을 투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        binding=FragmentDialogFolderRenameBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    private lateinit var oldName:String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oldName = requireArguments().getString("folderName")!!
        binding.textInputEditText.apply {
            setText(oldName)
        }
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.okButton.setOnClickListener { okButton() }

    }
    private fun okButton(){
        viewModel.checkTextView(binding.textInputEditText.text.toString())
        viewModel.checkTextViewResult.observe(this,observer)
    }
    private val observer = Observer<Int> {
        if (it == 0 && binding.textInputEditText.text.toString() != "") {
            // Folder 이름 변경 완료
            reNameFolder()
        } else {
            // Folder 이름 변경 실패
            if (it!=0){
                binding.textInputLayout.error="중복된 그룹이름"}
            else {binding.textInputLayout.error="그룹이름을 입력해주세요"}
        }
    }



    private fun reNameFolder(){
        val newName =binding.textInputEditText.text.toString()
        viewModel.reNameFolder(oldName,newName).observe(this, Observer {
            if(it=="finish"){
                findNavController().navigate(R.id.action_dialogFolderRename_to_editFragment)

            }
        })

    }


}

