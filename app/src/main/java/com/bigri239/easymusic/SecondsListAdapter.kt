package com.bigri239.easymusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SecondsListAdapter(val connector : MainActivity.Connector) : RecyclerView.Adapter<SecondsListAdapter.SecondsListViewHolder>() {


    private val sounds: MutableList<Sound> = mutableListOf()
    private val soundSeconds: MutableList<SoundSecond> = mutableListOf()
    private var lenLast = 700
    init {
        eraseSounds()
    }

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

    fun addSound(newSound: Sound) {
        sounds.add(newSound)
        initSecondSounds()
    }

    private fun initSecondSounds() {
        soundSeconds.clear()
        for (sound in sounds) {
            for (i in 0 until sound.shift) {
                soundSeconds.add(SoundSecond())
            }
            for (i in 0 until sound.length) {
                soundSeconds.add(SoundSecond(getColor(sound.type), sound))
            }
        }
        for (i in 0..lenLast) {
            soundSeconds.add(SoundSecond())
        }
        for (i in 1 until soundSeconds.size) {
            if (i % 50 == 0) {
                soundSeconds[i].color =  when((i / 50) % 4) {
                    0 -> R.color.line2
                    1 -> R.color.line3
                    2 -> R.color.line4
                    else -> R.color.line5
                }
            }
        }
        notifyDataSetChanged()
    }

    fun setLength (len: Int) {
        lenLast += len - itemCount
        val prevItemCount = itemCount
        for (i in 0..(len - prevItemCount)) {
            soundSeconds.add(SoundSecond())
        }
        for (i in prevItemCount until soundSeconds.size) {
            if (i % 50 == 0) {
                soundSeconds[i].color =  when((i / 50) % 4) {
                    0 -> R.color.line2
                    1 -> R.color.line3
                    2 -> R.color.line4
                    else -> R.color.line5
                }
            }
        }
        notifyDataSetChanged()
    }

    fun fillTrack (soundList: List<Sound>) {
        sounds.clear()
        sounds.addAll(soundList)
        initSecondSounds()
    }

    fun editSound (newSound : Sound) {
        sounds[newSound.number] = newSound
        initSecondSounds()
    }

    fun deleteSound (j : Int) {
        if (j != sounds.size - 1) sounds[j + 1].shift += sounds[j].length + sounds[j].shift
        sounds.removeAt(j)
        initSecondSounds()
    }

    private fun getColor(type: SoundType): Int {
        return when (type){
            SoundType.SOUND1 -> R.color.sound1
            SoundType.SOUND2 -> R.color.sound2
            SoundType.SOUND3 -> R.color.sound3
            SoundType.SOUND4 -> R.color.sound4
            SoundType.SOUND5 -> R.color.sound5
        }
    }

    fun eraseSounds() {
        sounds.clear()
        initSecondSounds()
    }

    inner class SecondsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val viewSquare: View = itemView.findViewById(R.id.viewSquare)
        fun bind(second: SoundSecond) {
            viewSquare.setBackgroundResource(second.color)
            viewSquare.setOnClickListener {
                second.sound?.let { if (second != SoundSecond()) {connector.function(it.track, it.number)} }
            }
        }
    }
}