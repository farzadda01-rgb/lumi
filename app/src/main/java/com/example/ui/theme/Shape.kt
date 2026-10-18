package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val LumiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),      // Chips
    medium = RoundedCornerShape(14.dp),     // Input fields
    large = RoundedCornerShape(20.dp),      // Standard cards
    extraLarge = RoundedCornerShape(24.dp)  // Large feature cards / bottom sheets
)

val LumiButtonShape = RoundedCornerShape(16.dp)
val LumiPillShape = RoundedCornerShape(50)
val LumiChipShape = RoundedCornerShape(12.dp)
val LumiInputFieldShape = RoundedCornerShape(14.dp)
val LumiCardShape = RoundedCornerShape(20.dp)
val LumiHeroCardShape = RoundedCornerShape(24.dp)
