package com.project.stockproject.search

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class SearchHistoryManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("search_history_queue1", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addSearchHistory(stockName: String, stockCode: String) {
        val searchHistoryDataList = getSearchHistoryDataList() //

        searchHistoryDataList.removeAll {
            it.stockCode==stockCode
        }

        // 크기가 20인 리스트를 유지
        if (searchHistoryDataList.size >= 10) {
            searchHistoryDataList.removeAt(0)
        }

        // 새로운 사용자 정보 추가
        searchHistoryDataList.add(HistoryManager(stockName, stockCode))

        // SharedPreferences에 저장
        saveUserDataList(searchHistoryDataList)
    }

    fun getSearchHistory() : List<HistoryManager> {
        val searchHistoryDataList = getSearchHistoryDataList()
       return searchHistoryDataList.reversed()
    }

    private fun getSearchHistoryDataList(): MutableList<HistoryManager> {
        val searchHistoryDataListJson = sharedPreferences.getString("search_history_list1", "[]")
        val type = object : TypeToken<MutableList<HistoryManager>>() {}.type
        return gson.fromJson(searchHistoryDataListJson, type)
    }

    private fun saveUserDataList(searchHistoryDataList: List<HistoryManager>) {
        val editor = sharedPreferences.edit()
        val userDataListJson = gson.toJson(searchHistoryDataList)
        editor.putString("search_history_list1", userDataListJson)
        editor.apply()
    }

    fun getStockNameByCode(stockCode: String): String? {
        val searchHistoryDataList = getSearchHistoryDataList()

        val foundItem = searchHistoryDataList.find { it.stockCode == stockCode }

        return foundItem?.stockName
    }





}