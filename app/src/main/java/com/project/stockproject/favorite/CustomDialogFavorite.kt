package com.project.stockproject.favorite

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import com.project.stockproject.MyViewModel
import com.project.stockproject.databinding.CustomDialogAddFavoriteBinding


class CustomDialogFavorite():DialogFragment() {
    private lateinit var binding: CustomDialogAddFavoriteBinding
    private lateinit var viewModel:MyViewModel
    private lateinit var favoriteDialogAdapter: FavoriteDialogAdapter
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

    override fun onResume() {
        super.onResume()
        val width = resources.getDimensionPixelSize(com.project.stockproject.R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(com.project.stockproject.R.dimen.popup_height)
        dialog!!.window!!.setLayout(width, height)
    }

    override fun onStart() {
        super.onStart()
        addFolder()//폴더 추가
        binding.buttonNO.setOnClickListener { buttonNO() } //취소버튼
        binding.buttonYES.setOnClickListener { buttonYES() } //확인버튼
        adapterSetting() //어뎁터 정의
    }

    //폴더 추가
    private fun addFolder(){
        binding.addTextView.setOnClickListener {
            if(binding.textInputLayout.isVisible){
                binding.textInputLayout.visibility=View.INVISIBLE
                binding.recyclerView.visibility=View.VISIBLE
            }else{
                binding.textInputLayout.visibility=View.VISIBLE
                binding.recyclerView.visibility=View.INVISIBLE
            }
        }
    }

    //취소버튼
    private fun buttonNO(){
        if(binding.textInputLayout.isVisible){ //폴더 추가일떄
            binding.textInputLayout.visibility=View.INVISIBLE
            binding.recyclerView.visibility=View.VISIBLE
        }else{//관심종목 추가일때
            dismiss()   //dialog 닫기
        }
    }
    //확인버튼
    private fun buttonYES(){
        if(binding.textInputLayout.isVisible){ //폴더 추가일떄
            binding.textInputLayout.visibility=View.INVISIBLE
            binding.recyclerView.visibility=View.VISIBLE
            //폴더 추가
            viewModel.addFolder(favoriteDialogAdapter.currentList.size,binding.textInputEdit.text.toString(),requireContext())
            getAll() //폴더 다 불러오기
        }else{//관심종목 추가일때
            //파일에 관심종목 추가, dialog 닫기
        }
    }

    //어댑터 정의
    private fun adapterSetting(){
        favoriteDialogAdapter= FavoriteDialogAdapter{
            //아이템 클릭
        }
        binding.recyclerView.apply {
            layoutManager= LinearLayoutManager(context)
            adapter=favoriteDialogAdapter
        }
        getAll() //폴더 다 불러오기
    }
    private fun getAll(){
        viewModel.getAll(requireContext())?.observe(this, Observer {
            favoriteDialogAdapter.submitList(it)
        })
    }
}