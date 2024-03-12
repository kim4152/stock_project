package com.project.stockproject.favorite.editItem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.stockproject.MainActivity
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.ItemTouchHelperCallback
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentEditModeBinding
import com.project.stockproject.room.ItemTable

class EditModeFragment : Fragment() {

    private lateinit var binding: FragmentEditModeBinding
    private lateinit var editModeAdapter: EditModeAdapter
    private lateinit var viewModel: MyViewModel
    private lateinit var folderName: String
    private lateinit var callback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        folderName = requireArguments().getString("folderName")!!
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        binding = FragmentEditModeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        binding.topAppBar.setNavigationOnClickListener { findNavController().navigate(R.id.action_editModeFragment_to_favoriteFragment) }
        binding.iconCheck.setOnClickListener { checkButton() }
        binding.deleteCons.setOnClickListener { deleteButton() }
        binding.moveItemCons.setOnClickListener { moveButton() }
        setBackpress()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setAdapter() {
        editModeAdapter = EditModeAdapter(
            ArrayList<EditModeItem>(),
            onClick = {
                it.isChecked = !it.isChecked
                editModeAdapter.notifyDataSetChanged()
            },
            viewModel,folderName,
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = editModeAdapter
        }
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(editModeAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        submit()
    }

    private fun submit() {
        viewModel.getAllItems(folderName)
        viewModel.getAllItemsResult.observe(this,observer)
    }
    val observer : Observer<List<ItemTable>> = Observer {
        editModeAdapter.adapterList= it.transform() as ArrayList<EditModeItem>
        Log.d("dafdsfa",editModeAdapter.adapterList.toString())
        editModeAdapter.notifyDataSetChanged()
    }

    private var checkButtonSelected = false
    private fun checkButton() {
        checkButtonSelected = (!checkButtonSelected)
        when (checkButtonSelected) {
            true -> { //전체 선택
                editModeAdapter.adapterList.forEach {
                    it.isChecked = true
                }
                editModeAdapter.notifyDataSetChanged()
            }

            false -> {//전체 선택 해제
                editModeAdapter.adapterList.forEach {
                    it.isChecked = false
                }
                editModeAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun deleteButton() {
        var tmpList: MutableList<String> = mutableListOf()
        editModeAdapter.adapterList.forEach {
            if (it.isChecked) tmpList.add(it.stockName)
        }
        if (tmpList.size > 0) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("종목삭제")
                .setMessage("종목 ${tmpList.size}개가 삭제됩니다.")

                .setNegativeButton("취소") { dialog, which ->

                }
                .setPositiveButton("삭제") { dialog, which ->
                    itemDelete(tmpList)
                }
                .show()
        } else {
            MyApplication.makeToast("삭제할 종목을 선택해주세요")
        }
    }

    private fun itemDelete(list: List<String>) {
        viewModel.itemDelete(folderName, list)
        viewModel.itemDeleteResult.observe(this, Observer {
            Log.d("dafdsfa",it)
            if (it == "end") {
                submit()
            }
        })
    }

    private fun moveButton() {

        val dialog = MaterialAlertDialogBuilder(requireContext())
        var itemIndex = 0
        val checked = editModeAdapter.adapterList.find { it.isChecked }
        if (checked != null) {
            viewModel.getAll()
            viewModel.getAllResult.observe(this, Observer {folderTable->
                val list = folderTable.map { it.folderName }
                dialog.apply {
                    setTitle("이동할 그룹 선택")
                    setSingleChoiceItems(list.toTypedArray(), 0) { _, which ->
                        itemIndex = which
                    }
                    setNegativeButton("취소") { _, _ -> }
                    setPositiveButton("이동") { _, which ->
                        if (itemIndex != -1) {
                            itemMove(list[itemIndex])
                        }

                    }
                    show()
                }
            })

        } else {
            MyApplication.makeToast("이동할 종목을 선택해주세요")
        }
    }

    private fun itemMove(newFolderName: String) {
        val itemList: MutableList<String> = editModeAdapter.adapterList
            .filter { it.isChecked }
            .map { it.stockName }
            .toMutableList()


        viewModel.itemCheck(newFolderName,itemList).observe(this, Observer {
            Log.d("dsfsdfs",it.toString())
            if (it>0 ){
                MyApplication.makeToast("동일한 종목이 있습니다")
            }else{
                //현재 폴더에서 아이템 삭제
                itemDelete(itemList)
                //새 폴더에 아이템 추가
                itemInsert(newFolderName)
            }
            })
    }


    private fun itemInsert(newFolderName: String) {
        val list = editModeAdapter.adapterList.filter { it.isChecked }
            .map { it }.toMutableList()
        list.forEach {
           viewModel.insertItem(it.stockName,newFolderName,it.stockCode)
        }
    }

    override fun onResume() {
        super.onResume()
        //bottom navigation 숨기기
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        //fragment 꺼질때 바텀네비 보이게
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(false)
    }
    private fun setBackpress(){
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로 가기 버튼 처리
                findNavController().navigate(R.id.action_editModeFragment_to_favoriteFragment)

            }
        }
    }
}

data class EditModeItem(
    val stockName: String,
    val stockCode: String,
    val folderName: String,
    var index: Int,
    var isChecked: Boolean,
)

fun List<ItemTable>.transform(): List<EditModeItem> {
    return this.map {
        EditModeItem(
            stockName = it.itemName,
            stockCode = it.itemCode,
            folderName = it.folderName,
            index = it.index!!,
            isChecked = false,
        )
    }
}