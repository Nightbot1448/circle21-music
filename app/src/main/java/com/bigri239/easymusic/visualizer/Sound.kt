package com.bigri239.easymusic.visualizer

data class Sound(
    var shift: Int,
    val length: Int,
    val type: SoundType,
    val track: Int,
    val number: Int
)