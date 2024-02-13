package com.project.stockproject.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.stockproject.MainActivity
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentFavoriteBinding
import com.project.stockproject.room.FolderTable

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: MyViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=ViewModelProviders.of(this)[MyViewModel::class.java]
        binding=FragmentFavoriteBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerSetting()
        viewPagerSetting()
        binding.edit.setOnClickListener { editClick() }
    }

    //textView dropdown
    private fun spinnerSetting(){
        val tmpList : MutableList<String> = mutableListOf()
        viewModel.getAll(requireContext())?.observe(this, Observer {
            if (it.isNotEmpty()){
                it.forEach {table->
                    tmpList.add(table.folderName)
                }
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,tmpList)
                binding.spinner.apply {
                    adapter = arrayAdapter
                    onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            binding.viewPager.currentItem=p2
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
                }
            }else{
                MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_MaterialAlertDialog_Center)
                    .setTitle("아직 관심그룹이 없습니다")
                    .setIcon(R.drawable.baseline_info_24)
                    .setPositiveButton("추가"){_,_->
                        findNavController().navigate(R.id.action_favoriteFragment_to_editFragment)
                    }
                    .setNegativeButton("취소"){_,_->}
                    .show()
            }

        })
    }

    //편집
    private fun editClick(){
        findNavController().navigate(R.id.action_favoriteFragment_to_editFragment)
    }
    //viewPager2
    private fun viewPagerSetting(){
        val viewPager = binding.viewPager
        var folderName:List<FolderTable> = listOf()
        viewModel.getAll(requireContext())?.observe(this, Observer {
            folderName=it
            viewPager.adapter=FavoriteAdapter(it,viewModel,this)
        })
        viewPager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.spinner.setSelection(position)
                }
            })
        }
    }



}