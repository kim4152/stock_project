package com.project.stockproject.favorite

import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import com.project.stockproject.MyViewModel
import com.project.stockproject.databinding.CustomDialogAddFavoriteBinding


class CustomDialogFavorite() : DialogFragment() {
    private lateinit var binding: CustomDialogAddFavoriteBinding
    private lateinit var viewModel: MyViewModel
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
        binding = CustomDialogAddFavoriteBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
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
        totalCountSet()

    }

    //총 폴더 개수
    private fun totalCountSet(){
        viewModel.count().observe(this, Observer {
            binding.totalTextView.text="총 ${it}개"
        })
    }


    //폴더 추가
    private fun addFolder() {
        binding.addTextView.setOnClickListener {
            if (binding.textInputLayout.isVisible) {
                invisibleTextView()
                binding.textInputEdit.text = null
            } else {
                visibleTextView()
            }
        }
    }

    //취소버튼
    private fun buttonNO() {
        if (binding.textInputLayout.isVisible) { //폴더 추가일떄
            invisibleTextView()
            binding.textInputEdit.text = null
        } else {//관심종목 추가일때
            dismiss()   //dialog 닫기
        }
    }
    //확인버튼
    //observer를 호출할때마다 새로운 인스턴스 생성을 막기
    private val observer: Observer<Int?> = Observer {
        if (it == 0 && binding.textInputEdit.text.toString() != "") {
            // Folder 추가 완료
            addFolerComplete()
        } else {
            // Folder 추가 실패
            if (it!=0) binding.textInputLayout.error="중복된 그룹이름"
            else binding.textInputLayout.error="그룹이름을 입력해주세요"
        }
    }
    private fun buttonYES() {
        if (binding.textInputLayout.isVisible) { //폴더 추가일떄
            viewModel.checkTextView(binding.textInputEdit.text.toString())
            viewModel.checkTextViewResult.observe(this, observer)
        } else {//관심종목 추가일때
            //파일에 관심종목 추가, dialog 닫기
        }
    }

    //folder 이름 검사

    //room에 폴더 저장 & 리사이클러뷰 업데이트
    private fun addFolerComplete() {
        invisibleTextView()
        //폴더 추가
        viewModel.addFolder(
            favoriteDialogAdapter.currentList.size + 1,
            binding.textInputEdit.text.toString(),
            requireContext()
        )
        viewModel.addFolderResult.observe(this, Observer {
            if (it == "end") {
                getAll() //폴더 다 불러오기
                binding.textInputEdit.text = null
            }
        })
    }

    //어댑터 정의
    private fun adapterSetting() {
        favoriteDialogAdapter = FavoriteDialogAdapter {
            //아이템 클릭
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteDialogAdapter
        }
        getAll() //폴더 다 불러오기
    }

    private fun getAll() {
        viewModel.getAll(requireContext())?.observe(this, Observer {
            favoriteDialogAdapter.submitList(it)
        })
    }

    //키보드 내리기
    @SuppressLint("ServiceCast")
    fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //textView 보이기
    private fun visibleTextView() {
        binding.textInputLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.INVISIBLE
        binding.textInputLayout.error=null
    }

    //textView 숨기기
    private fun invisibleTextView() {
        binding.textInputLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.textInputLayout.error=null
        view?.let { hideKeyboard(it) }
    }
}