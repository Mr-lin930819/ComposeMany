package com.mrlin.composemany.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Blue200 = Color.Blue.convert(ColorSpaces.CieLab).copy(green = -100f)
val Blue500 = Color.Blue.convert(ColorSpaces.CieLab).copy(green = -60f)
val Blue700 = Color.Blue.convert(ColorSpaces.CieLab).copy(green = 0f)