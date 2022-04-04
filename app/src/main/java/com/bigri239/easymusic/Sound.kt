package com.bigri239.easymusic.recyclers

import androidx.annotation.ColorRes
import com.bigri239.easymusic.R

data class Sound( // смотри data class SoundInfo!!!!!!!!
    val shift: Int,
    val length: Int,
    val type: SoundType,
    val track: Int
)

enum class SoundType { SOUND1, SOUND2, SOUND3, SOUND4, SOUND5 }

data class SoundSecond(
    @ColorRes val color: Int = R.color.white,
    val sound: Sound? = null
)