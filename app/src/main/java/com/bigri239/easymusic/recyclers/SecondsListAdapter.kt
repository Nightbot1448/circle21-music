package com.bigri239.easymusic.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigri239.easymusic.R

class SecondsListAdapter : RecyclerView.Adapter<SecondsListAdapter.SecondsListViewHolder>() {

    private val sounds: List<Sound> = listOf()
    private val soundSeconds: List<SoundSecond> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecondsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_second, parent, false)
        return SecondsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SecondsListViewHolder, position: Int) {
        holder.bind(soundSeconds[position])
    }

    override fun getItemCount(): Int {
        return soundSeconds.size
    }



    class SecondsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val viewSquare: View = itemView.findViewById(R.id.viewSquare)

        fun bind(second: SoundSecond) {
            viewSquare.setBackgroundResource(second.color)
        }
    }
}