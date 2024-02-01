package com.project.stockproject.stockInform.tabFragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentTabfourthBinding
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.stockInform.StockInformFragment
import com.project.stockproject.stockInform.StockInformFragment.Companion.STOCKOUTPUT
import com.project.stockproject.stockInform.StockOutput
import org.jsoup.Jsoup

import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL


class TabfourthFragment : Fragment() {
    private lateinit var binding: FragmentTabfourthBinding
    private lateinit var newsAdapter:NewsAdapter
    private lateinit var viewModel: TabViewModel
    private lateinit var searchManager: SearchHistoryManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTabfourthBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel=ViewModelProviders.of(this)[TabViewModel::class.java]
        searchManager = SearchHistoryManager(MyApplication.getAppContext())
        val item= STOCKOUTPUT
        val stockName = item?.let { searchManager.getStockNameByCode(it.stck_shrn_iscd) }
        setAdapter()//adapter setting

        if (stockName != null) {
            viewModel.getNews(stockName).observe(this, Observer {
                newsAdapter.submitList(it)
            })
        }

    }

    private fun setAdapter(){
        newsAdapter=NewsAdapter{
            startActivity(Intent(activity,NewsWebViewActivity::class.java).apply {
                putExtra("url",it)
            })
        }
        binding.newsRecyclerView.apply {
            layoutManager= LinearLayoutManager(context)
            adapter=newsAdapter
        }
    }


}