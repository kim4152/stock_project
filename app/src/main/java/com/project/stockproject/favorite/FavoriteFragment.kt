package com.project.stockproject.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.SharedViewModel
import com.project.stockproject.common.ViewPagerSharedPreferences
import com.project.stockproject.databinding.FragmentFavoriteBinding
import com.project.stockproject.room.FolderTable


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding

    private lateinit var viewModel: MyViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var sharedPreferences: ViewPagerSharedPreferences
    private lateinit var favoriteAdapter: FavoriteAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = ViewPagerSharedPreferences(requireContext())
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerSetting()
        chipSetting()

        binding.searchBar.setOnClickListener { showMenu() }
        binding.edit.setOnClickListener { editClick() }
    }


    private lateinit var popupMenu: PopupMenu
    private fun showMenu() {
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            handleMenu(item)
            true
        }

        popupMenu.show()
    }

    private fun handleMenu(item: MenuItem){
        val pos = item.itemId+(popupMenu.menu.size())*10
        binding.viewPager.setCurrentItem(pos,true)
    }

    private fun makeMenu(it: List<FolderTable>) {
        popupMenu = PopupMenu(requireContext(), binding.searchBar)
        val menuList = it.map { map -> map.folderName }

        for ((index, itemText) in menuList.withIndex()) {
            popupMenu.menu.add(Menu.NONE, index, Menu.NONE, itemText)
        }
    }

    private fun chipSetting() {
        val chip1 = binding.chip1
        val chip2 = binding.chip2

        chip1.setOnClickListener {
            if (chip1.isChecked) {
                chip1.isChecked = true
                chip2.isChecked = false
                sharedViewModel.updateData("current")
            } else {
                chip1.isChecked = false
                chip2.isChecked = true
                sharedViewModel.updateData("predic")
            }
        }

        chip2.setOnClickListener {
            if (chip2.isChecked) {
                chip1.isChecked = false
                chip2.isChecked = true
                sharedViewModel.updateData("predic")
            } else {
                chip1.isChecked = true
                chip2.isChecked = false
                sharedViewModel.updateData("current")
            }
        }
    }

    //편집
    private fun editClick() {
        findNavController().navigate(R.id.action_favoriteFragment_to_editFragment)
    }



    private val viewPagerObserver: Observer<List<FolderTable>> = Observer {
        makeMenu(it)

        if (it.isNotEmpty()) {
            binding.viewPager.apply {
                favoriteAdapter = FavoriteAdapter(it, activity!!)
                adapter = favoriteAdapter

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)

                        if (it.isNotEmpty()) binding.searchBar.text =
                            it[position].folderName

                        if (it.isNotEmpty()){
                            sharedPreferences.setPosition("viewPager33", position)
                        }
                    }
                })
                val pos = sharedPreferences.getPosition("viewPager33")
                setCurrentItem(pos, true)
                sharedViewModel.updateData("current") //관심종목 리스트 현재가로 세팅
            }
        }

    }


    //viewPager2
    private fun viewPagerSetting() {
        viewModel.getAll()
        viewModel.getAllResult.observe(this, viewPagerObserver)
    }
}