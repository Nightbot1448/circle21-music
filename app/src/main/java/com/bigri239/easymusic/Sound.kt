package com.bigri239.easymusic

import androidx.annotation.ColorRes

data class Sound(
    val shift: Int,
    val length: Int,
    val type: SoundType,
    val track: Int,
    val number: Int
)

enum class SoundType { SOUND1, SOUND2, SOUND3, SOUND4, SOUND5 }

data class SoundSecond(
    @ColorRes val color: Int = R.color.white,
    val sound: Sound? = null
)