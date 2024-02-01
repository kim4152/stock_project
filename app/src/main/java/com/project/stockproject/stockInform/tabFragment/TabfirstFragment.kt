package com.project.stockproject.stockInform.tabFragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.stockproject.R
import com.project.stockproject.stockInform.StockOutput


class TabfirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("adfdsf","1:onCreateView")
        return inflater.inflate(R.layout.fragment_tabfirst, container, false)
    }




}