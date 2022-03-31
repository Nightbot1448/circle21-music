package com.bigri239.easymusic

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(private val animals: MutableList<String>)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflate the custom view from xml layout file
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom,parent,false)

        // return the view holder
        return ViewHolder(view)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // display the current animal
        holder.animal.text = animals[position]
        holder.animal.setOnClickListener {
Log.d("debug", holder.animal.text.toString())

        }
    }


    override fun getItemCount(): Int {
        // number of items in the data set held by the adapter
        return animals.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val animal= itemView.findViewById<TextView>(R.id.tvAnimal)
    }


    // this two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }
}