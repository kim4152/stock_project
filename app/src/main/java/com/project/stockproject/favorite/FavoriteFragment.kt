package com.project.stockproject.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentFavoriteBinding
import com.project.stockproject.room.FolderTable


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding

    private lateinit var viewModel: MyViewModel
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
            } else {
                chip1.isChecked = false
                chip2.isChecked = true
            }
        }

        chip2.setOnClickListener {
            if (chip2.isChecked) {
                chip1.isChecked = false
                chip2.isChecked = true
            } else {

                chip1.isChecked = true
                chip2.isChecked = false
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
                            it[position % it.size].folderName.toString()

                        if (it.isNotEmpty()) sharedPreferences.setPosition(
                            "viewPager",
                            position % it.size
                        )
                    }
                })
                val pos = sharedPreferences.getPosition("viewPager") + (it.size) * 10
                setCurrentItem(pos, true)
            }
        }

    }


    //viewPager2
    private fun viewPagerSetting() {
        viewModel.getAll()
        viewModel.getAllResult.observe(this, viewPagerObserver)
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("aaabbb","onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("aaabbb","onStart")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("aaabbb","destroyView")
        _binding=null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("aaabbb","destroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("dfsdf","onDetach")
    }*/

}