package com.project.stockproject.favorite

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.stockproject.MainActivity
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.AddDialogBinding
import com.project.stockproject.databinding.EditItemBinding
import com.project.stockproject.databinding.FragmentEditBinding
import com.project.stockproject.room.transform

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var binding2: AddDialogBinding
    private lateinit var editAdapter: EditAdapter
    private lateinit var viewModel: MyViewModel
    private lateinit var addDialog: AlertDialog
    //observer를 호출할때마다 새로운 인스턴스 생성을 막기
    private val observer: Observer<Int?> = Observer {
        if (it == 0 && binding2.textView.text.toString() != "") {
            // Folder 추가 완료
            Log.d("adfd", it.toString())
            addFolerComplete(binding2)
            addDialog.dismiss() //다이얼로그 닫기
            removePatentView()//다이얼로그 한번 더 띄울 때 부모뷰 초기화
        } else {
            // Folder 추가 실패
            if (it != 0) {
                makeDialog("중복된 그룹이름")
            } else {
                makeDialog("그룹이름 없음")
            }

        }
    }
    private fun makeDialog(title:String){
        MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_MaterialAlertDialog_Center)
            .setIcon(R.drawable.baseline_info_24)
            .setTitle(title)
            .setPositiveButton("확인"){_,_->}
            .setCancelable(false)
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bottom navigation 숨기기
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        binding = FragmentEditBinding.inflate(layoutInflater, container, false)
        binding2 = AddDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        binding.iconCheck.setOnClickListener { checkButton() }
        binding.iconDelete.setOnClickListener { deleButton() }
        binding.iconAdd.setOnClickListener { addButton() }
        //appbar 뒤로가기 클릭
        binding.topAppBar.setNavigationOnClickListener { findNavController().navigate(R.id.action_editFragment_to_favoriteFragment) }
    }


    //추가버튼
    private fun addButton() {

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("그룹추가")
            .setView(binding2.root)
            .setOnCancelListener { removePatentView() }
            .setNegativeButton("취소") { _, _ ->
                removePatentView()//다이얼로그 한번 더 띄울 때 부모뷰 초기화
            }
            .setPositiveButton("추가") { _, _ ->

            }
        addDialog = builder.create()
        addDialog.show()
        addDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            viewModel.checkTextView(binding2.textView.text.toString())
            viewModel.checkTextViewResult.observe(this, observer)
        }
    }

    //다이얼로그 한번 더 띄울 때 부모뷰 초기화
    private fun removePatentView() {
        val parent = binding2.root.parent as? ViewGroup
        parent?.removeView(binding2.root)
    }


    //db에 저장
    private fun addFolerComplete(binding2: AddDialogBinding) {

        //폴더 추가
        viewModel.addFolder(
            editAdapter.currentList.size + 1,
            binding2.textView.text.toString(),
            requireContext()
        )
        viewModel.addFolderResult.observe(this, Observer {
            if (it == "end") {
                getAll() //폴더 다 불러오기
                binding2.textView.text = null
            }
        })
    }

    private fun getAll() {
        viewModel.getAll(requireContext())?.observe(this, Observer {
            editAdapter.submitList(it.transform())
        })
    }

    //삭제버튼
    private fun deleButton() {
        var tmpList: MutableList<String> = mutableListOf()
        editAdapter.currentList.forEach {
            Log.d("afdfasdf", it.folderName + ":" + it.isSelected)
            if (it.isSelected) tmpList.add(it.folderName)
        }
        if (tmpList.size > 0) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("그룹삭제")
                .setMessage("${tmpList.toString()}가 삭제됩니다.")

                .setNegativeButton("취소") { dialog, which ->

                }
                .setPositiveButton("삭제") { dialog, which ->
                    folderDelete(tmpList)
                }
                .show()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("그룹삭제")
                .setMessage("삭제할 그룹을 선택해주세요.")
                .setPositiveButton("확인") { dialog, which ->
                }
                .show()
        }
    }

    //폴더 삭제
    private fun folderDelete(list: List<String>) {
        viewModel.folderDelete(list)
        viewModel.folderDeleteResult.observe(this, Observer {
            if (it == "end") {
                getAll(false)
            }
        })
    }

    //radio버튼(전체 선택) 설정
    private var checkButtonSelected = false
    private fun checkButton() {

        checkButtonSelected = (!checkButtonSelected)
        when (checkButtonSelected) {
            true -> { //전체 선택
                editAdapter.currentList.forEach {
                    it.isSelected = true
                }
                editAdapter.notifyDataSetChanged()
            }

            false -> {//전체 선택 해제
                editAdapter.currentList.forEach {
                    it.isSelected = false
                }
                editAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun setAdapter() {
        editAdapter = EditAdapter {
            //아이템 클릭
            it.isSelected = (!it.isSelected)
            editAdapter.notifyDataSetChanged()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = editAdapter
        }
        getAll(false)  //전체 폴더 불러오기
    }

    private fun getAll(isSelected: Boolean) {
        var tmpList: MutableList<EditItem> = mutableListOf()
        viewModel.getAll(requireContext())?.observe(this, Observer {
            it.forEach { folerTable ->
                tmpList.add(EditItem(folerTable.folderName, isSelected))
            }
            editAdapter.submitList(tmpList)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        //fragment 꺼질때 바텀네비 보이게
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(false)
    }

}

data class EditItem(
    val folderName: String,
    var isSelected: Boolean,
)