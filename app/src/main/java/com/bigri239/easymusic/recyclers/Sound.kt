package com.bigri239.easymusic.recyclers

import androidx.annotation.ColorRes

data class Sound(
    val shift: Int,
    val length: Int,
    val type: SoundType
)

enum class SoundType { SOUND1, SOUND2, SOUND3, SOUND4, SOUND5 }

data class SoundSecond(
    @ColorRes val color: Int,
    val sound: Sound
)