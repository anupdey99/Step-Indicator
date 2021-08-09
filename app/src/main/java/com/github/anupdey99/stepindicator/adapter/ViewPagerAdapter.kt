package com.github.anupdey99.stepindicator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.anupdey99.stepindicator.databinding.ItemViewPagerBinding

class ViewPagerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.pageNumber.text = "Page ${model}"
        }
    }

    inner class ViewHolder(val binding: ItemViewPagerBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    fun loadInitData(list: MutableList<String>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}