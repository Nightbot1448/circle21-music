package com.bigri239.easymusic.visualizer

import androidx.annotation.ColorRes
import com.bigri239.easymusic.R

data class SoundSecond(
    @ColorRes var color: Int = R.color.white,
    val sound: Sound? = null
)
