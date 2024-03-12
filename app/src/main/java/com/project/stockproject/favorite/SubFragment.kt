package com.project.stockproject.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.databinding.FavoriteViewpagerBinding
import com.project.stockproject.room.FolderTable
import com.project.stockproject.room.ItemTable

class SubFragment(private val folderTable: FolderTable):Fragment() {
    private lateinit var binding: FavoriteViewpagerBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var subAdapter: SubAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=ViewModelProviders.of(this)[MyViewModel::class.java]
        binding = FavoriteViewpagerBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllItems(folderTable.folderName)
        viewModel.getAllItemsResult.observe(this,observer)
    }
    private val observer:Observer<List<ItemTable>> = Observer {
        subAdapter= SubAdapter(it.transform(),viewModel,this,
            editMode = {
                val bundle = bundleOf("folderName" to folderTable.folderName)
                findNavController().navigate(
                    R.id.action_favoriteFragment_to_editModeFragment,
                    bundle
                )
                //sharedPreferences.setPosition("viewPager", binding.viewPager.currentItem)
            },
            subOnClick = { click ->
                val bundle =
                    bundleOf("stockCode" to click.stockCode, "stockName" to click.stockName
                        ,"folderName" to folderTable.folderName)
                findNavController().navigate(
                    R.id.action_favoriteFragment_to_stockInformFragment, bundle
                )
            },
        )
        binding.recyclerView.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=subAdapter
        }
    }



    data class SubItem(
        val stockName: String,
        val stockCode: String,
        var isChecked: Boolean,
    )

    fun List<ItemTable>.transform(): List<SubItem> {
        return this.map {
            SubItem(
                stockName = it.itemName,
                stockCode = it.itemCode,
                isChecked = false,
            )
        }
    }
}