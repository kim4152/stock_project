package com.project.stockproject.home.multiFactor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.CustomDecoration
import com.project.stockproject.databinding.FragmentMFSubBinding
import com.project.stockproject.home.MFItem
import com.project.stockproject.stockInform.StockList

class MFSubFragment(private val list:List<MFItem>, val position:Int) : Fragment() {
    private lateinit var binding:FragmentMFSubBinding
    private lateinit var multiFactorAdapter:MultiFactorAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMFSubBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myViewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        binding.multiFactorRecyclerView.apply {
            multiFactorAdapter = MultiFactorAdapter(onClick = {
                val bundle = bundleOf("stockName" to it.stock_name, "stockCode" to it.stock_code,)
                findNavController().navigate(R.id.action_homeFragment_to_stockInformFragment,bundle)
            },position,myViewModel, this@MFSubFragment)
            layoutManager = LinearLayoutManager(context)
            adapter = multiFactorAdapter
            addItemDecoration(
                CustomDecoration(1f,10f,
                ContextCompat.getColor(requireContext(),R.color.outlineVariant))
            )
        }

        multiFactorAdapter.submitList(list)
    }



}