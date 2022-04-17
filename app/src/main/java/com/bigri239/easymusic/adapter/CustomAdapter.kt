package com.bigri239.easymusic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bigri239.easymusic.R

internal class CustomAdapter(private var itemsList: List<String>, val connector: CustomConnector) :

    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.itemTextView)
        fun bind(string: String) {
            itemTextView.setOnClickListener {
                connector.function(string)
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item1, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.itemTextView.text = item
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}