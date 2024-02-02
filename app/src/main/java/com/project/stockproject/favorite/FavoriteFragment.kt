package com.project.stockproject.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
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
        dropDown()
        viewPagerSetting()
        binding.edit.setOnClickListener { editClick() }
    }

    //textView dropdown
    private fun dropDown(){
        val tmpList : MutableList<String> = mutableListOf()
        viewModel.getAll(requireContext())?.observe(this, Observer {
            it.forEach {table->
                tmpList.add(table.folderName)
            }
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,tmpList)
            binding.textView.setAdapter(arrayAdapter)
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
            viewPager.adapter=FavoriteAdapter(it)
        })
        viewPager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textView.setText(folderName[position].folderName)
                }
            })
        }
    }



}