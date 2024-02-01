package com.project.stockproject.stockInform.tabFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.databinding.NewsItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(private val onClick:(String)->Unit) : ListAdapter<NewsModel, NewsAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding:NewsItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: NewsModel){
            binding.titleTextViews.text=item.title
            binding.root.setOnClickListener {
                onClick(item.link)
            }

            binding.date.text=dateFormatChange(item.date)
        }
    }

    private fun dateFormatChange(input: String): String {

        // 입력 형식 지정
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        val date = inputFormat.parse(input)

        // 출력 형식 지정
        val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA)
        return outputFormat.format(date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NewsItemBinding.inflate(
                LayoutInflater.from(parent.context)
                ,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<NewsModel>(){
            override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                return oldItem===newItem //hash cod 비교
            }

            override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                return oldItem==newItem
            }

        }
    }

}