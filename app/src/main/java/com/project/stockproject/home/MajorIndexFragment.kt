package com.project.stockproject.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.stockproject.R
import com.project.stockproject.databinding.FragmentMajorIndexBinding

class MajorIndexFragment : Fragment() {
    private lateinit var binding: FragmentMajorIndexBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMajorIndexBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


}