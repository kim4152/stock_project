package com.project.stockproject.favorite.editFolder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
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
import com.project.stockproject.databinding.FragmentEditBinding
import com.project.stockproject.room.FolderTable
import com.project.stockproject.room.transform

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var binding2: AddDialogBinding
    private lateinit var editAdapter: EditAdapter
    private lateinit var viewModel: MyViewModel
    private lateinit var addDialog: AlertDialog
    private lateinit var callback: OnBackPressedCallback


    private fun makeDialog(title: String) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_App_MaterialAlertDialog_Center
        )
            .setIcon(R.drawable.baseline_info_24)
            .setTitle(title)
            .setPositiveButton("확인") { _, _ -> }
            .setCancelable(false)
            .show()
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
        binding.checkCons.setOnClickListener { checkButton() }
        binding.deleteCons.setOnClickListener { deleButton() }
        binding.iconAdd.setOnClickListener { addButton() }
        binding.renameCons.setOnClickListener { renameButton() }
        binding.editCons.setOnClickListener { editButton() }
        //appbar 뒤로가기 클릭
        binding.topAppBar.setNavigationOnClickListener { findNavController().navigate(R.id.action_editFragment_to_favoriteFragment) }
        setBackpress()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onResume() {
        super.onResume()
        //bottom navigation 숨기기
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(true)
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

    private val observer = Observer<Int> {
        if (it == 0 && binding2.textView.text.toString() != "") {
            // Folder 추가 완료
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
        viewModel.getAll()
        viewModel.getAllResult.observe(this, Observer {
            editAdapter.submitList(it.transform())
        })
    }

    //삭제버튼
    private fun deleButton() {
        var tmpList: MutableList<String> = mutableListOf()
        editAdapter.currentList.forEach {
            if (it.isSelected) tmpList.add(it.folderName)
        }
        if (tmpList.size > 0) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("그룹삭제")
                .setMessage("그룹 ${tmpList.size}개가 삭제됩니다.")

                .setNegativeButton("취소") { dialog, which ->

                }
                .setPositiveButton("삭제") { dialog, which ->
                    folderDelete(tmpList)
                }
                .show()
        } else {
            MyApplication.makeToast("삭제할 그룹을 선택해주세요")
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
        editAdapter = EditAdapter(
            onClick = {
                it.isSelected = (!it.isSelected)

                val count = editAdapter.currentList.count { item ->
                    item.isSelected
                }
                if (count > 1) {
                    binding.renameCons.visibility = View.INVISIBLE
                    binding.editCons.visibility = View.INVISIBLE
                } else {
                    binding.renameCons.visibility = View.VISIBLE
                    binding.editCons.visibility = View.VISIBLE
                }

                editAdapter.notifyDataSetChanged()
            },
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = editAdapter
        }
        getAll(false)  //전체 폴더 불러오기
    }

    private fun renameButton() {
        val checked = editAdapter.currentList.find {
            it.isSelected
        }
        if (checked != null) {
            changeFolderName(checked.folderName)
        } else {
            MyApplication.makeToast("관심그룹을 선택해주세요")
        }
    }

    //관심그룹 이름 바꾸기(다이얼로그)
    private fun changeFolderName(folderName: String) {
        val bundle = bundleOf("folderName" to folderName)

        findNavController().navigate(R.id.action_editFragment_to_dialogFolderRename, bundle)

    }

    private fun editButton() {
        val checked = editAdapter.currentList.find {
            it.isSelected
        }
        if (checked != null) {
            val bundle = bundleOf("folderName" to checked.folderName)
            findNavController().navigate(R.id.action_editFragment_to_editModeFragment, bundle)
        } else {
            MyApplication.makeToast("관심그룹을 선택해주세요")
        }

    }

    private fun getAll(isSelected: Boolean) {

        viewModel.getAll()
        viewModel.getAllResult.observe(this, getAllObserver)
    }
    private val getAllObserver : Observer<List<FolderTable>> = Observer {
        var tmpList: MutableList<EditItem> = mutableListOf()
        it.forEach { folerTable ->
            Log.d("dadfdf",folerTable.folderName)
            tmpList.add(EditItem(folerTable.folderName, false))
        }
        Log.d("dadfdf",tmpList.toString())
        editAdapter.submitList(tmpList)
    }

    override fun onDestroy() {
        super.onDestroy()
        //fragment 꺼질때 바텀네비 보이게
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(false)
    }

    private fun setBackpress() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로 가기 버튼 처리
                findNavController().navigate(R.id.action_editFragment_to_favoriteFragment)
            }
        }
    }
}


data class EditItem(
    val folderName: String,
    var isSelected: Boolean,
)