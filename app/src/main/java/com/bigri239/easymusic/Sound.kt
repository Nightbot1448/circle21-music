package com.bigri239.easymusic

import androidx.annotation.ColorRes

data class Sound(
    var shift: Int,
    val length: Int,
    val type: SoundType,
    val track: Int,
    val number: Int
)

enum class SoundType { SOUND1, SOUND2, SOUND3, SOUND4, SOUND5, SOUND11, SOUND12, SOUND13, SOUND14, SOUND15, SOUND21, SOUND22, SOUND23, SOUND24, SOUND25 }

data class SoundSecond(
    @ColorRes var color: Int = R.color.white,
    val sound: Sound? = null
)